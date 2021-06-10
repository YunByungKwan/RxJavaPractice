# RxJavaPractice

## Flowable

- 생산자: 1 ~ 300개의 아이템 생산(딜레이 없음) (1 ~ 10,000개로 해도 같은 결과 나옴)
- 소비자: 0.1초마다 소비
<br>

#### <결과>

- Default: 버퍼가 꽉 찰때까지 생산하고 기다렸다가 생산하고.. 조절이 가능한 듯 하다.
- onBackpressureBuffer(ERROR): Exception이 발생한다. Exception 처리시 1개의 아이템만 소비한다.
- onBackpressureBuffer(DROP_OLDEST):
  - 버퍼 크기(128)만큼 소비한다 -> 그 후 남은 양에 대해서 버퍼 크기를 초과하는 양은 오래된 것부터 버린다 -> 그리고 남은 버퍼 크기(128)만큼 소비한다.
- onBackpressureBuffer(DROP_LATEST):
  - 버퍼 크기 약 2배(255개)를 소비한 후 -> 가장 최신의 데이터 1개 빼고 남은 것을 버린다 -> 가장 최신의 데이터를 소비한다.
- onBackpressureDrop(): 버퍼 크기(128)만큼 소비한다 -> 남은 데이터를 모두 버린다.
- onBackpressureLatest(): 버퍼 크기(128)만큼 소비한다 -> 가장 최신의 데이터 1개 빼고 모두 버린다 -> 가장 최신의 데이터를 소비한다.
<br>
<img src="https://user-images.githubusercontent.com/51109517/121502599-483fca80-ca1b-11eb-8ac1-cb8073f5e4c2.png" width=900 height=550/>
<img src="https://user-images.githubusercontent.com/51109517/121502224-ee3f0500-ca1a-11eb-8b01-a7b78784a7b6.png" width=900 height=550/>
