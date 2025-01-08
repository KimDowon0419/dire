-> 마크 다운 형식으로 넣어야함 글쓰기,수정,조회,삭제| 기본적인 회원가입,로그인 기능 추가 - 예외처리 추가 필요


아래는 **Multipass**로 4대(혹은 3대) VM을 만들고, **쿠버네티스 클러스터**를 구성한 뒤, 마지막으로 **Jenkins**를 배포하는 과정을 **Markdown 문서**로 정리한 예시입니다. **처음 보는 사람**도 어느 정도 이해할 수 있도록, **히스토리**와 **어려웠던 점**을 구체적으로 담았습니다.

> **Note**: 문서에 등장하는 코드/명령어는 **필수적으로 참고가 필요한** 최소한의 범위만 포함했습니다. -> vmsetting 에서 확인할수 있다.

---

# Multipass + Kubernetes + Jenkins 구축 히스토리

## 개요
이번 프로젝트에서는 **macOS** 환경에서 [Multipass]라는 툴을 사용해 **가상 머신(VM)**을 생성하고, 그 위에 **쿠버네티스 클러스터**를 구성했습니다. 이후 **Helm 차트**를 활용해 **Jenkins** 서버를 배포해보았습니다.

- 호스트: macOS + Multipass
- VM 수: 4대 (Control Plane 1대, Worker Node 3대)
- 컨테이너 런타임: CRI-O
- 네트워킹: Flannel(CNI)
- Jenkins 배포: Helm + NodePort

아래 단계별로 작업 내용과 어려웠던 점을 정리해두었습니다.

---

## 1. Multipass VM 생성

- **목표**: VM 4대를 생성 (control, worker1, worker2, worker3)
- **메모리/CPU**: 각 VM에 CPU 2~4개, 메모리 2~4GB 할당
- **어려웠던 점**:
  - macOS에서 NAT 설정이 제대로 안 되면 외부 인터넷 접근이 막힐 수 있음.
  - VM 이름(예: `control`, `worker1`)을 중복 없이 신경 써서 지정해야 함.

**필요 최소 명령 예시**:

```bash
# VM 생성 예시
multipass launch --name control --cpus 2 --mem 4G --disk 10G
multipass launch --name worker1 --cpus 2 --mem 2G --disk 10G
multipass launch --name worker2 --cpus 2 --mem 2G --disk 10G
multipass launch --name worker3 --cpus 2 --mem 2G --disk 10G
```

---

## 2. VM 초기 구성

1. **접속**: `multipass shell control` (또는 worker1, worker2, worker3)
2. **패키지 업데이트**: `sudo apt-get update && sudo apt-get upgrade -y`
3. **커널 모듈/네트워크 설정**
   - `br_netfilter` 모듈 로드, `net.bridge.bridge-nf-call-iptables=1` 설정
   - `net.ipv4.ip_forward=1`로 IP 포워딩 활성화

```bash
sudo modprobe br_netfilter
sudo sysctl -w net.bridge.bridge-nf-call-iptables=1
sudo sysctl -w net.ipv4.ip_forward=1
```

- **어려웠던 점**:
  - 재부팅 후에도 설정이 유지되도록 `/etc/sysctl.d/99-bridge.conf` 등에 내용을 기록해야 함.
  - 모듈을 `/etc/modules-load.d/br_netfilter.conf`에 넣지 않으면, VM 재시작 후 설정이 사라질 수 있음.

---

## 3. 쿠버네티스 설치 및 클러스터 구성

- **런타임**: CRI-O (또는 containerd)
- **설치 과정**:
  1. **control** 노드에서 `kubeadm init --pod-network-cidr=10.244.0.0/16 ...`
  2. CNI(Flannel) 적용:
     ```bash
     kubectl apply -f https://raw.githubusercontent.com/flannel-io/flannel/master/Documentation/kube-flannel.yml
     ```
  3. 워커 노드에서 `kubeadm join ...` 명령으로 클러스터 합류

- **어려웠던 점**:
  - **Taint**가 있어 Control 노드에 DaemonSet(Flannel) 배포가 안 되거나,
  - br_netfilter 설정이 안 되어 Flannel이 CrashLoopBackOff 에 빠지는 문제 발생.
  - 구체적으로 `/run/flannel/subnet.env: no such file or directory` 에러를 해소하기 위해 모든 노드에서 `br_netfilter`를 로드해줘야 했음.

---

## 4. DNS & 외부 인터넷 접근 문제

- **문제 상황**: Jenkins init 시 `updates.jenkins.io`에 접속해야 하는데, 쿠버네티스 클러스터 내부에서 DNS가 `SERVFAIL` 발생.
- **원인**: macOS NAT + VPN/방화벽 설정으로 UDP 53 요청이 제대로 전달되지 않거나, `CoreDNS`가 `/etc/resolv.conf`를 통해 `192.168.64.1:53`에 질의하지만 응답 없음.
- **해결책**(중 하나):
  - `kubectl edit configmap coredns -n kube-system` 후, CoreDNS `forward . 8.8.8.8 { ... }` 식으로 **직접 Public DNS**를 지정.
  - 그래도 ping(icmp) 안 되는 건 정상(방화벽/권한). `curl`은 정상적으로 HTTPS가 열리면 문제 없음.

---

## 5. Jenkins Helm 차트 배포

1. **Helm 설치**(맥 또는 Control VM)
2. **Jenkins 차트 레포 추가**:
   ```bash
   helm repo add jenkinsci https://charts.jenkins.io
   helm repo update
   ```
3. **배포**:
   ```bash
   helm install jenkins jenkinsci/jenkins \
     --namespace jenkins \
     --create-namespace \
     --set controller.serviceType=NodePort \
     --set controller.nodePort=30080
   ```
4. **어려움**:
   - Jenkins가 init 컨테이너로 플러그인 목록을 다운로드할 때 DNS 실패로 `UnknownHostException` 발생.
   - CoreDNS, NAT 이슈 해결 후 재배포하니 정상 동작.

---

## 6. Jenkins 초기 비밀번호 & NodePort 접근

Helm 차트 설치 후 출력되는 **NOTES** 섹션에 안내된 명령으로 **admin 비밀번호**와 **접속 URL**을 확인:

```bash
# admin 비밀번호
kubectl exec --namespace jenkins -it svc/jenkins \
  -c jenkins -- /bin/cat /run/secrets/additional/chart-admin-password
```

**NodePort** 방식이므로 `<Worker Node IP>:30080`으로 브라우저에서 접근 가능.
- 이후 **Jenkins 웹UI**에서 `admin` + (위에서 구한 password)로 로그인.

---

## 7. VM을 꺼도 데이터가 유지되는지?

- **VM을 stop**해도, Multipass의 **가상 디스크**는 유지됨.
- 쿠버네티스가 PVC(`persistence.enabled=true`)로 Jenkins 데이터를 저장하는 한, 다시 VM을 켜면 Jenkins도 이전 상태 그대로 복원됨.
- 다만, **`persistence.enabled=false`**라면 Pod 재배포 시 데이터가 날아갈 수 있으니 주의.

---

## 어려움을 겪은 포인트 정리

1. **Flannel CrashLoopBackOff**
   - br_netfilter 등 커널 모듈 미설정 → `/run/flannel/subnet.env` 파일 미생성
2. **DNS/외부 접근 실패**
   - macOS NAT + VPN 환경에서 CoreDNS가 `/etc/resolv.conf`를 통한 UDP 53 질의를 성공하지 못함.
   - 해결: CoreDNS `forward . 8.8.8.8` or `force_tcp`
3. **Jenkins 플러그인 다운로드 실패**
   - DNS 해석 불가 → `updates.jenkins.io` UnknownHostException
   - CoreDNS 설정 수정 후 재배포로 해결
4. **VM을 끄는 경우**
   - Multipass stop → 가상 디스크는 남아있으므로, start 후 재부팅 시 쿠버네티스 & Jenkins 재등장
   - 완전히 날려면 `multipass delete <vm>` + `multipass purge` 해야 함

---

## 결론

- **macOS + Multipass + Kubernetes** 조합은 **NAT/방화벽** 문제가 발생하기 쉽지만, 커널 모듈 설정과 CoreDNS 포워딩을 잘 조정하면 성공적으로 클러스터를 운영 가능.
- **Jenkins**는 Helm 차트로 간단히 배포할 수 있으며, PVC가 있으면 **VM을 꺼도 데이터**가 안전하게 유지됨.
- 이번 프로젝트를 통해, **로컬 환경**에서 여러 VM을 띄우고 **쿠버네티스 클러스터**를 구성하는 과정을 학습했으며, **DNS 문제 해결**과 **Helm 배포** 경험을 쌓게 되었다.

---
