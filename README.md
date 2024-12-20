### ASMR(Access Search Mobile Radar) 앱 : 중요 파일 및 카메라 접근감시 앱
https://github.com/sadikuls/FileObserver 기반 개발 → 특정 디렉터리 파일 생성 감시 앱

* 모바일 내 파일 및 카메라 모니터링 (감시)을 위한 앱 개발 프로젝트
* 안드로이드 내부에서 파일 생성, 접근, 삭제, 변경 등의 이벤트 탐지 기능 및 카메라 접근 탐지 기능을 구현
* 기존 앱과의 차이점 : Same - 로그 기록 및 확인, Diff - 접근한 앱, 사용자 알림
* 주요기능
> 1) 카메라에 접근하는 프로세스 확인 및 알림
> 2) 선택한 파일에 발생하는 이벤트 알림
> 3) 여러 파일 또는 디렉토리 모니터링
> 4) 목표 기기에서 일어나는 모든 활동에 대한 시스템 레벨의 모니터링
* 프로젝트 간 역할 : 팀장, 앱 개발, 모의침투 기반 앱 기능 시연, 발표
<br/><br/>
### 개발배경
* 앱 설치 시 초기 접근권한을 허용 → 이후엔 앱에서 언제, 어떻게 접근하는지 알 수 없음 Free Pass
* 권한허용의 1회성, 권한이 필요한 이유에 대한 신뢰도 없음
* 내가 허용해준 권한이라도 어떤 앱에서, 언제, 어떤 권한을 사용하는지 알 필요가 있음

<br/><br/>
### 흐름도
![image](https://github.com/user-attachments/assets/41e805ac-1859-41f5-b1cf-a67385f64550)

<br/><br/>
#### Select Activity
* 파일 감시를 위한 MainActivity 호출
* 앱 감시를 위한 MainActivity2 호출

<br/><br/>
### 프로젝트 시연
> 1) 사용자가 인지하지 못한 악성앱이 파일 생성 시 탐지
<br/><br/>
[![Video Label](http://img.youtube.com/vi/4JNEtwXX8mM/0.jpg)](https://youtu.be/4JNEtwXX8mM?t=0s)
<br/><br/>
> 3) 사용자가 인지하지 못한 악성앱이 카메라 접근 시 탐지
<br/><br/>
[![Video Label](http://img.youtube.com/vi/Tupw4Mod8yI/0.jpg)](https://youtu.be/Tupw4Mod8yI?t=0s)


