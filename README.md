# RxJavaPractice

## Flowable

### BackpressureOverflow 전략(버퍼가 가득 찼을 때 추가로 실행하는 전략)

#### <요약>

1 - 50,000,000의 데이터를 발행 후 테스트 진행.

|   Option    | Strategy                                                     | Result                            |
| :---------: | :----------------------------------------------------------- | :-------------------------------- |
|    ERROR    | MissingBackpressureException예외를 던지고 데이터 흐름을 중단 | MissingBackpressureException 발생 |
| DROP_OLDEST | 버퍼에 쌓여 있는 가장 오래된 값을 제거                       | 1 ~ 128, 49999873 ~ 50000000      |
| DROP_LATEST | 버퍼에 쌓여 있는 최근 값을 제거                              | 1 ~ 255, 50000000                 |

#### <테스트 상세 내용>

<b>데이터 방출 ></b>

```kotlin
object Repository {
    fun getData_Flowable(): Flowable<Int> {
        return Flowable.range(1, 50_000_000)
    }
}
```

- <b>BackpressureOverflowStrategy.ERROR</b>

  ```kotlin
  Repository.getData_Flowable()
      .onBackpressureBuffer(128, {}, BackpressureOverflowStrategy.ERROR)
      .observeOn(Schedulers.computation())
      .subscribe {
          Thread.sleep(100)
          val endTime = System.currentTimeMillis()
          Log.d("TAG", "[${endTime - startTime}] $it")
          tempList.add(it)
          _itemList.postValue(tempList)
       }
  ```

  결과: MissingBackpressureException이 발생하고 바로 종료된다.

  ```
      --------- beginning of crash
  2021-06-06 01:11:37.287 28930-308/com.kwancorp.asyncapp2 E/AndroidRuntime: FATAL EXCEPTION: RxComputationThreadPool-1
    Process: com.kwancorp.asyncapp2, PID: 28930
    io.reactivex.exceptions.OnErrorNotImplementedException: The exception was not handled due to missing onError handler in the subscribe() method call. Further reading: https://github.com/ReactiveX/RxJava/wiki/Error-Handling | io.reactivex.exceptions.MissingBackpressureException
        at io.reactivex.internal.functions.Functions$OnErrorMissingConsumer.accept(Functions.java:704)
  
  ```

  <br>

- <b>BackpressureOverflowStrategy.DROP_OLDEST</b>

  ```kotlin
  Repository.getData_Flowable()
      .onBackpressureBuffer(128, {}, BackpressureOverflowStrategy.DROP_OLDEST)
      .observeOn(Schedulers.computation())
      .subscribe {
          Thread.sleep(100)
          val endTime = System.currentTimeMillis()
          Log.d("TAG", "[${endTime - startTime}] $it")
          tempList.add(it)
          _itemList.postValue(tempList)
      }
  ```

  결과
  1 ~ 128까지 출력되고 그 다음에 49999873 ~ 50000000이 출력된다.<br>
  <img src="https://user-images.githubusercontent.com/51109517/120900424-b0835a80-c66f-11eb-867d-2a03ec90fd77.gif" width=200 height=400/>

  <details>
  <summary>결과 로그</summary>
  <div markdown="1">
  <br>


  ```
  2021-06-06 01:23:05.830 30065-1504/com.kwancorp.asyncapp2 D/TAG: [121] 1
  2021-06-06 01:23:05.930 30065-1504/com.kwancorp.asyncapp2 D/TAG: [221] 2
  2021-06-06 01:23:06.030 30065-1504/com.kwancorp.asyncapp2 D/TAG: [321] 3
  2021-06-06 01:23:06.131 30065-1504/com.kwancorp.asyncapp2 D/TAG: [421] 4
  2021-06-06 01:23:06.231 30065-1504/com.kwancorp.asyncapp2 D/TAG: [522] 5
  2021-06-06 01:23:06.331 30065-1504/com.kwancorp.asyncapp2 D/TAG: [622] 6
  2021-06-06 01:23:06.431 30065-1504/com.kwancorp.asyncapp2 D/TAG: [722] 7
  2021-06-06 01:23:06.531 30065-1504/com.kwancorp.asyncapp2 D/TAG: [822] 8
  2021-06-06 01:23:06.632 30065-1504/com.kwancorp.asyncapp2 D/TAG: [923] 9
  2021-06-06 01:23:06.732 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1023] 10
  2021-06-06 01:23:06.833 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1124] 11
  2021-06-06 01:23:06.933 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1224] 12
  2021-06-06 01:23:07.034 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1325] 13
  2021-06-06 01:23:07.134 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1425] 14
  2021-06-06 01:23:07.235 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1526] 15
  2021-06-06 01:23:07.336 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1627] 16
  2021-06-06 01:23:07.437 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1728] 17
  2021-06-06 01:23:07.538 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1829] 18
  2021-06-06 01:23:07.639 30065-1504/com.kwancorp.asyncapp2 D/TAG: [1929] 19
  2021-06-06 01:23:07.739 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2030] 20
  2021-06-06 01:23:07.840 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2131] 21
  2021-06-06 01:23:07.941 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2232] 22
  2021-06-06 01:23:08.042 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2333] 23
  2021-06-06 01:23:08.143 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2434] 24
  2021-06-06 01:23:08.244 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2535] 25
  2021-06-06 01:23:08.345 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2636] 26
  2021-06-06 01:23:08.446 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2737] 27
  2021-06-06 01:23:08.547 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2838] 28
  2021-06-06 01:23:08.648 30065-1504/com.kwancorp.asyncapp2 D/TAG: [2939] 29
  2021-06-06 01:23:08.749 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3040] 30
  2021-06-06 01:23:08.850 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3141] 31
  2021-06-06 01:23:08.951 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3242] 32
  2021-06-06 01:23:09.052 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3343] 33
  2021-06-06 01:23:09.153 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3444] 34
  2021-06-06 01:23:09.253 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3544] 35
  2021-06-06 01:23:09.354 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3645] 36
  2021-06-06 01:23:09.455 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3746] 37
  2021-06-06 01:23:09.556 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3847] 38
  2021-06-06 01:23:09.657 30065-1504/com.kwancorp.asyncapp2 D/TAG: [3948] 39
  2021-06-06 01:23:09.758 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4048] 40
  2021-06-06 01:23:09.858 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4149] 41
  2021-06-06 01:23:09.959 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4250] 42
  2021-06-06 01:23:10.060 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4351] 43
  2021-06-06 01:23:10.161 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4452] 44
  2021-06-06 01:23:10.262 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4552] 45
  2021-06-06 01:23:10.363 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4653] 46
  2021-06-06 01:23:10.464 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4754] 47
  2021-06-06 01:23:10.565 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4855] 48
  2021-06-06 01:23:10.666 30065-1504/com.kwancorp.asyncapp2 D/TAG: [4956] 49
  2021-06-06 01:23:10.767 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5057] 50
  2021-06-06 01:23:10.867 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5158] 51
  2021-06-06 01:23:10.968 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5259] 52
  2021-06-06 01:23:11.069 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5360] 53
  2021-06-06 01:23:11.170 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5460] 54
  2021-06-06 01:23:11.270 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5561] 55
  2021-06-06 01:23:11.371 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5662] 56
  2021-06-06 01:23:11.472 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5763] 57
  2021-06-06 01:23:11.573 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5864] 58
  2021-06-06 01:23:11.674 30065-1504/com.kwancorp.asyncapp2 D/TAG: [5965] 59
  2021-06-06 01:23:11.775 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6065] 60
  2021-06-06 01:23:11.875 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6166] 61
  2021-06-06 01:23:11.977 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6267] 62
  2021-06-06 01:23:12.077 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6368] 63
  2021-06-06 01:23:12.179 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6469] 64
  2021-06-06 01:23:12.279 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6570] 65
  2021-06-06 01:23:12.381 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6671] 66
  2021-06-06 01:23:12.482 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6772] 67
  2021-06-06 01:23:12.584 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6875] 68
  2021-06-06 01:23:12.686 30065-1504/com.kwancorp.asyncapp2 D/TAG: [6976] 69
  2021-06-06 01:23:12.787 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7078] 70
  2021-06-06 01:23:12.888 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7179] 71
  2021-06-06 01:23:12.991 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7282] 72
  2021-06-06 01:23:13.092 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7383] 73
  2021-06-06 01:23:13.193 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7484] 74
  2021-06-06 01:23:13.294 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7585] 75
  2021-06-06 01:23:13.396 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7686] 76
  2021-06-06 01:23:13.497 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7788] 77
  2021-06-06 01:23:13.598 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7889] 78
  2021-06-06 01:23:13.700 30065-1504/com.kwancorp.asyncapp2 D/TAG: [7990] 79
  2021-06-06 01:23:13.801 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8092] 80
  2021-06-06 01:23:13.903 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8193] 81
  2021-06-06 01:23:14.004 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8294] 82
  2021-06-06 01:23:14.105 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8396] 83
  2021-06-06 01:23:14.207 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8497] 84
  2021-06-06 01:23:14.308 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8599] 85
  2021-06-06 01:23:14.409 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8700] 86
  2021-06-06 01:23:14.511 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8801] 87
  2021-06-06 01:23:14.612 30065-1504/com.kwancorp.asyncapp2 D/TAG: [8903] 88
  2021-06-06 01:23:14.713 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9004] 89
  2021-06-06 01:23:14.814 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9105] 90
  2021-06-06 01:23:14.916 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9206] 91
  2021-06-06 01:23:15.018 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9309] 92
  2021-06-06 01:23:15.119 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9410] 93
  2021-06-06 01:23:15.220 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9511] 94
  2021-06-06 01:23:15.321 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9612] 95
  2021-06-06 01:23:15.422 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9713] 96
  2021-06-06 01:23:15.525 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9815] 97
  2021-06-06 01:23:15.626 30065-1504/com.kwancorp.asyncapp2 D/TAG: [9916] 98
  2021-06-06 01:23:15.727 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10018] 99
  2021-06-06 01:23:15.828 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10119] 100
  2021-06-06 01:23:15.930 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10220] 101
  2021-06-06 01:23:16.031 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10322] 102
  2021-06-06 01:23:16.132 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10423] 103
  2021-06-06 01:23:16.234 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10524] 104
  2021-06-06 01:23:16.335 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10626] 105
  2021-06-06 01:23:16.436 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10727] 106
  2021-06-06 01:23:16.537 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10828] 107
  2021-06-06 01:23:16.639 30065-1504/com.kwancorp.asyncapp2 D/TAG: [10929] 108
  2021-06-06 01:23:16.740 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11031] 109
  2021-06-06 01:23:16.842 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11132] 110
  2021-06-06 01:23:16.943 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11234] 111
  2021-06-06 01:23:17.044 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11335] 112
  2021-06-06 01:23:17.146 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11436] 113
  2021-06-06 01:23:17.247 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11537] 114
  2021-06-06 01:23:17.348 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11639] 115
  2021-06-06 01:23:17.450 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11741] 116
  2021-06-06 01:23:17.551 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11842] 117
  2021-06-06 01:23:17.653 30065-1504/com.kwancorp.asyncapp2 D/TAG: [11943] 118
  2021-06-06 01:23:17.754 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12045] 119
  2021-06-06 01:23:17.855 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12146] 120
  2021-06-06 01:23:17.956 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12247] 121
  2021-06-06 01:23:18.057 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12348] 122
  2021-06-06 01:23:18.159 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12449] 123
  2021-06-06 01:23:18.260 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12551] 124
  2021-06-06 01:23:18.361 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12652] 125
  2021-06-06 01:23:18.462 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12753] 126
  2021-06-06 01:23:18.564 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12855] 127
  2021-06-06 01:23:18.665 30065-1504/com.kwancorp.asyncapp2 D/TAG: [12956] 128
  2021-06-06 01:23:18.766 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13057] 49999873
  2021-06-06 01:23:18.867 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13158] 49999874
  2021-06-06 01:23:18.968 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13259] 49999875
  2021-06-06 01:23:19.070 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13360] 49999876
  2021-06-06 01:23:19.171 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13462] 49999877
  2021-06-06 01:23:19.273 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13563] 49999878
  2021-06-06 01:23:19.374 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13665] 49999879
  2021-06-06 01:23:19.475 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13766] 49999880
  2021-06-06 01:23:19.577 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13868] 49999881
  2021-06-06 01:23:19.678 30065-1504/com.kwancorp.asyncapp2 D/TAG: [13969] 49999882
  2021-06-06 01:23:19.780 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14070] 49999883
  2021-06-06 01:23:19.881 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14172] 49999884
  2021-06-06 01:23:19.983 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14273] 49999885
  2021-06-06 01:23:20.084 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14375] 49999886
  2021-06-06 01:23:20.186 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14476] 49999887
  2021-06-06 01:23:20.288 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14579] 49999888
  2021-06-06 01:23:20.390 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14680] 49999889
  2021-06-06 01:23:20.491 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14781] 49999890
  2021-06-06 01:23:20.592 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14883] 49999891
  2021-06-06 01:23:20.693 30065-1504/com.kwancorp.asyncapp2 D/TAG: [14984] 49999892
  2021-06-06 01:23:20.795 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15085] 49999893
  2021-06-06 01:23:20.896 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15187] 49999894
  2021-06-06 01:23:20.998 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15288] 49999895
  2021-06-06 01:23:21.099 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15390] 49999896
  2021-06-06 01:23:21.201 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15492] 49999897
  2021-06-06 01:23:21.302 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15593] 49999898
  2021-06-06 01:23:21.403 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15694] 49999899
  2021-06-06 01:23:21.504 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15795] 49999900
  2021-06-06 01:23:21.605 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15896] 49999901
  2021-06-06 01:23:21.706 30065-1504/com.kwancorp.asyncapp2 D/TAG: [15997] 49999902
  2021-06-06 01:23:21.807 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16098] 49999903
  2021-06-06 01:23:21.908 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16199] 49999904
  2021-06-06 01:23:22.009 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16300] 49999905
  2021-06-06 01:23:22.111 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16402] 49999906
  2021-06-06 01:23:22.213 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16504] 49999907
  2021-06-06 01:23:22.315 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16606] 49999908
  2021-06-06 01:23:22.417 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16707] 49999909
  2021-06-06 01:23:22.518 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16809] 49999910
  2021-06-06 01:23:22.620 30065-1504/com.kwancorp.asyncapp2 D/TAG: [16910] 49999911
  2021-06-06 01:23:22.723 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17013] 49999912
  2021-06-06 01:23:22.824 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17115] 49999913
  2021-06-06 01:23:22.925 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17215] 49999914
  2021-06-06 01:23:23.027 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17317] 49999915
  2021-06-06 01:23:23.128 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17419] 49999916
  2021-06-06 01:23:23.229 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17520] 49999917
  2021-06-06 01:23:23.331 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17621] 49999918
  2021-06-06 01:23:23.432 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17723] 49999919
  2021-06-06 01:23:23.533 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17824] 49999920
  2021-06-06 01:23:23.635 30065-1504/com.kwancorp.asyncapp2 D/TAG: [17925] 49999921
  2021-06-06 01:23:23.736 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18027] 49999922
  2021-06-06 01:23:23.837 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18128] 49999923
  2021-06-06 01:23:23.939 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18230] 49999924
  2021-06-06 01:23:24.040 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18331] 49999925
  2021-06-06 01:23:24.142 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18432] 49999926
  2021-06-06 01:23:24.247 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18537] 49999927
  2021-06-06 01:23:24.347 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18638] 49999928
  2021-06-06 01:23:24.449 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18740] 49999929
  2021-06-06 01:23:24.551 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18841] 49999930
  2021-06-06 01:23:24.652 30065-1504/com.kwancorp.asyncapp2 D/TAG: [18942] 49999931
  2021-06-06 01:23:24.754 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19045] 49999932
  2021-06-06 01:23:24.855 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19146] 49999933
  2021-06-06 01:23:24.958 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19247] 49999934
  2021-06-06 01:23:25.061 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19352] 49999935
  2021-06-06 01:23:25.162 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19453] 49999936
  2021-06-06 01:23:25.265 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19556] 49999937
  2021-06-06 01:23:25.367 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19657] 49999938
  2021-06-06 01:23:25.468 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19759] 49999939
  2021-06-06 01:23:25.569 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19860] 49999940
  2021-06-06 01:23:25.671 30065-1504/com.kwancorp.asyncapp2 D/TAG: [19961] 49999941
  2021-06-06 01:23:25.772 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20063] 49999942
  2021-06-06 01:23:25.874 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20164] 49999943
  2021-06-06 01:23:25.975 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20266] 49999944
  2021-06-06 01:23:26.077 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20368] 49999945
  2021-06-06 01:23:26.178 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20469] 49999946
  2021-06-06 01:23:26.279 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20570] 49999947
  2021-06-06 01:23:26.380 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20671] 49999948
  2021-06-06 01:23:26.481 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20772] 49999949
  2021-06-06 01:23:26.583 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20874] 49999950
  2021-06-06 01:23:26.685 30065-1504/com.kwancorp.asyncapp2 D/TAG: [20976] 49999951
  2021-06-06 01:23:26.786 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21077] 49999952
  2021-06-06 01:23:26.889 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21180] 49999953
  2021-06-06 01:23:26.990 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21281] 49999954
  2021-06-06 01:23:27.091 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21382] 49999955
  2021-06-06 01:23:27.192 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21483] 49999956
  2021-06-06 01:23:27.294 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21585] 49999957
  2021-06-06 01:23:27.396 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21686] 49999958
  2021-06-06 01:23:27.497 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21788] 49999959
  2021-06-06 01:23:27.599 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21889] 49999960
  2021-06-06 01:23:27.700 30065-1504/com.kwancorp.asyncapp2 D/TAG: [21990] 49999961
  2021-06-06 01:23:27.801 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22092] 49999962
  2021-06-06 01:23:27.903 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22193] 49999963
  2021-06-06 01:23:28.004 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22295] 49999964
  2021-06-06 01:23:28.106 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22396] 49999965
  2021-06-06 01:23:28.207 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22498] 49999966
  2021-06-06 01:23:28.308 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22599] 49999967
  2021-06-06 01:23:28.409 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22700] 49999968
  2021-06-06 01:23:28.511 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22802] 49999969
  2021-06-06 01:23:28.613 30065-1504/com.kwancorp.asyncapp2 D/TAG: [22904] 49999970
  2021-06-06 01:23:28.714 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23005] 49999971
  2021-06-06 01:23:28.816 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23107] 49999972
  2021-06-06 01:23:28.917 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23208] 49999973
  2021-06-06 01:23:29.019 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23310] 49999974
  2021-06-06 01:23:29.120 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23411] 49999975
  2021-06-06 01:23:29.221 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23512] 49999976
  2021-06-06 01:23:29.323 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23614] 49999977
  2021-06-06 01:23:29.424 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23715] 49999978
  2021-06-06 01:23:29.526 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23816] 49999979
  2021-06-06 01:23:29.627 30065-1504/com.kwancorp.asyncapp2 D/TAG: [23918] 49999980
  2021-06-06 01:23:29.728 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24019] 49999981
  2021-06-06 01:23:29.830 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24121] 49999982
  2021-06-06 01:23:29.931 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24222] 49999983
  2021-06-06 01:23:30.033 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24324] 49999984
  2021-06-06 01:23:30.134 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24425] 49999985
  2021-06-06 01:23:30.236 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24527] 49999986
  2021-06-06 01:23:30.337 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24628] 49999987
  2021-06-06 01:23:30.438 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24729] 49999988
  2021-06-06 01:23:30.539 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24829] 49999989
  2021-06-06 01:23:30.640 30065-1504/com.kwancorp.asyncapp2 D/TAG: [24930] 49999990
  2021-06-06 01:23:30.741 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25032] 49999991
  2021-06-06 01:23:30.842 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25133] 49999992
  2021-06-06 01:23:30.944 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25234] 49999993
  2021-06-06 01:23:31.045 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25336] 49999994
  2021-06-06 01:23:31.146 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25437] 49999995
  2021-06-06 01:23:31.247 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25538] 49999996
  2021-06-06 01:23:31.350 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25641] 49999997
  2021-06-06 01:23:31.451 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25741] 49999998
  2021-06-06 01:23:31.552 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25843] 49999999
  2021-06-06 01:23:31.654 30065-1504/com.kwancorp.asyncapp2 D/TAG: [25945] 50000000
  ```

  <br>
  </div>
  </details>
  <br>

- <b>BackpressureOverflowStrategy.DROP_LATEST</b>

  ```kotlin
  Repository.getData_Flowable()
            .onBackpressureBuffer(128, {}, BackpressureOverflowStrategy.DROP_LATEST)
            .observeOn(Schedulers.computation())
            .subscribe {
                Thread.sleep(100)
                val endTime = System.currentTimeMillis()
                Log.d("TAG", "[${endTime - startTime}] $it")
  
                tempList.add(it)
                _itemList.postValue(tempList)
            }
  ```

  결과: 1 ~ 255까지 출력되고 그 다음에 50000000이 바로 출력된다.<br>
  <img src="https://user-images.githubusercontent.com/51109517/120900645-e7a63b80-c670-11eb-9761-0f57dcaace36.gif" width=200 height=400/>

  <details>
  <summary>결과 로그</summary>
  <div markdown="1">
  <br>


  ```
  2021-06-06 02:36:58.930 6122-10994/com.kwancorp.asyncapp2 D/TAG: [133] 1
  2021-06-06 02:36:59.031 6122-10994/com.kwancorp.asyncapp2 D/TAG: [234] 2
  2021-06-06 02:36:59.132 6122-10994/com.kwancorp.asyncapp2 D/TAG: [335] 3
  2021-06-06 02:36:59.233 6122-10994/com.kwancorp.asyncapp2 D/TAG: [435] 4
  2021-06-06 02:36:59.333 6122-10994/com.kwancorp.asyncapp2 D/TAG: [536] 5
  2021-06-06 02:36:59.434 6122-10994/com.kwancorp.asyncapp2 D/TAG: [637] 6
  2021-06-06 02:36:59.535 6122-10994/com.kwancorp.asyncapp2 D/TAG: [738] 7
  2021-06-06 02:36:59.636 6122-10994/com.kwancorp.asyncapp2 D/TAG: [839] 8
  2021-06-06 02:36:59.737 6122-10994/com.kwancorp.asyncapp2 D/TAG: [940] 9
  2021-06-06 02:36:59.838 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1041] 10
  2021-06-06 02:36:59.938 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1141] 11
  2021-06-06 02:37:00.039 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1242] 12
  2021-06-06 02:37:00.140 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1342] 13
  2021-06-06 02:37:00.240 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1443] 14
  2021-06-06 02:37:00.340 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1543] 15
  2021-06-06 02:37:00.440 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1643] 16
  2021-06-06 02:37:00.540 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1743] 17
  2021-06-06 02:37:00.641 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1844] 18
  2021-06-06 02:37:00.741 6122-10994/com.kwancorp.asyncapp2 D/TAG: [1944] 19
  2021-06-06 02:37:00.841 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2044] 20
  2021-06-06 02:37:00.941 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2144] 21
  2021-06-06 02:37:01.042 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2245] 22
  2021-06-06 02:37:01.142 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2345] 23
  2021-06-06 02:37:01.242 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2445] 24
  2021-06-06 02:37:01.343 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2546] 25
  2021-06-06 02:37:01.446 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2649] 26
  2021-06-06 02:37:01.547 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2750] 27
  2021-06-06 02:37:01.647 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2850] 28
  2021-06-06 02:37:01.747 6122-10994/com.kwancorp.asyncapp2 D/TAG: [2950] 29
  2021-06-06 02:37:01.847 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3050] 30
  2021-06-06 02:37:01.948 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3151] 31
  2021-06-06 02:37:02.048 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3251] 32
  2021-06-06 02:37:02.148 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3351] 33
  2021-06-06 02:37:02.249 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3452] 34
  2021-06-06 02:37:02.349 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3552] 35
  2021-06-06 02:37:02.450 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3652] 36
  2021-06-06 02:37:02.550 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3753] 37
  2021-06-06 02:37:02.650 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3853] 38
  2021-06-06 02:37:02.751 6122-10994/com.kwancorp.asyncapp2 D/TAG: [3954] 39
  2021-06-06 02:37:02.851 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4054] 40
  2021-06-06 02:37:02.951 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4154] 41
  2021-06-06 02:37:03.051 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4254] 42
  2021-06-06 02:37:03.152 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4355] 43
  2021-06-06 02:37:03.252 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4455] 44
  2021-06-06 02:37:03.352 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4555] 45
  2021-06-06 02:37:03.452 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4655] 46
  2021-06-06 02:37:03.553 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4756] 47
  2021-06-06 02:37:03.653 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4856] 48
  2021-06-06 02:37:03.754 6122-10994/com.kwancorp.asyncapp2 D/TAG: [4957] 49
  2021-06-06 02:37:03.854 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5057] 50
  2021-06-06 02:37:03.955 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5158] 51
  2021-06-06 02:37:04.055 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5258] 52
  2021-06-06 02:37:04.156 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5359] 53
  2021-06-06 02:37:04.256 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5459] 54
  2021-06-06 02:37:04.357 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5559] 55
  2021-06-06 02:37:04.457 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5660] 56
  2021-06-06 02:37:04.558 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5761] 57
  2021-06-06 02:37:04.659 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5862] 58
  2021-06-06 02:37:04.759 6122-10994/com.kwancorp.asyncapp2 D/TAG: [5962] 59
  2021-06-06 02:37:04.859 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6062] 60
  2021-06-06 02:37:04.959 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6162] 61
  2021-06-06 02:37:05.060 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6263] 62
  2021-06-06 02:37:05.160 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6363] 63
  2021-06-06 02:37:05.260 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6463] 64
  2021-06-06 02:37:05.360 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6563] 65
  2021-06-06 02:37:05.461 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6664] 66
  2021-06-06 02:37:05.561 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6764] 67
  2021-06-06 02:37:05.661 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6864] 68
  2021-06-06 02:37:05.761 6122-10994/com.kwancorp.asyncapp2 D/TAG: [6964] 69
  2021-06-06 02:37:05.862 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7065] 70
  2021-06-06 02:37:05.962 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7165] 71
  2021-06-06 02:37:06.062 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7265] 72
  2021-06-06 02:37:06.162 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7365] 73
  2021-06-06 02:37:06.262 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7465] 74
  2021-06-06 02:37:06.363 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7566] 75
  2021-06-06 02:37:06.463 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7666] 76
  2021-06-06 02:37:06.563 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7766] 77
  2021-06-06 02:37:06.664 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7867] 78
  2021-06-06 02:37:06.764 6122-10994/com.kwancorp.asyncapp2 D/TAG: [7967] 79
  2021-06-06 02:37:06.864 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8067] 80
  2021-06-06 02:37:06.964 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8167] 81
  2021-06-06 02:37:07.065 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8268] 82
  2021-06-06 02:37:07.165 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8368] 83
  2021-06-06 02:37:07.265 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8468] 84
  2021-06-06 02:37:07.366 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8568] 85
  2021-06-06 02:37:07.467 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8670] 86
  2021-06-06 02:37:07.569 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8772] 87
  2021-06-06 02:37:07.672 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8874] 88
  2021-06-06 02:37:07.773 6122-10994/com.kwancorp.asyncapp2 D/TAG: [8976] 89
  2021-06-06 02:37:07.874 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9077] 90
  2021-06-06 02:37:07.977 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9179] 91
  2021-06-06 02:37:08.079 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9282] 92
  2021-06-06 02:37:08.181 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9384] 93
  2021-06-06 02:37:08.282 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9484] 94
  2021-06-06 02:37:08.383 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9585] 95
  2021-06-06 02:37:08.483 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9686] 96
  2021-06-06 02:37:08.585 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9788] 97
  2021-06-06 02:37:08.686 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9889] 98
  2021-06-06 02:37:08.787 6122-10994/com.kwancorp.asyncapp2 D/TAG: [9990] 99
  2021-06-06 02:37:08.888 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10090] 100
  2021-06-06 02:37:08.989 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10191] 101
  2021-06-06 02:37:09.089 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10292] 102
  2021-06-06 02:37:09.190 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10393] 103
  2021-06-06 02:37:09.291 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10494] 104
  2021-06-06 02:37:09.393 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10595] 105
  2021-06-06 02:37:09.495 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10698] 106
  2021-06-06 02:37:09.596 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10799] 107
  2021-06-06 02:37:09.697 6122-10994/com.kwancorp.asyncapp2 D/TAG: [10899] 108
  2021-06-06 02:37:09.798 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11001] 109
  2021-06-06 02:37:09.899 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11102] 110
  2021-06-06 02:37:09.999 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11202] 111
  2021-06-06 02:37:10.101 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11303] 112
  2021-06-06 02:37:10.202 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11404] 113
  2021-06-06 02:37:10.302 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11505] 114
  2021-06-06 02:37:10.403 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11606] 115
  2021-06-06 02:37:10.504 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11707] 116
  2021-06-06 02:37:10.605 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11808] 117
  2021-06-06 02:37:10.705 6122-10994/com.kwancorp.asyncapp2 D/TAG: [11908] 118
  2021-06-06 02:37:10.806 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12009] 119
  2021-06-06 02:37:10.907 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12110] 120
  2021-06-06 02:37:11.008 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12211] 121
  2021-06-06 02:37:11.109 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12312] 122
  2021-06-06 02:37:11.209 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12412] 123
  2021-06-06 02:37:11.310 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12513] 124
  2021-06-06 02:37:11.411 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12614] 125
  2021-06-06 02:37:11.512 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12714] 126
  2021-06-06 02:37:11.612 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12815] 127
  2021-06-06 02:37:11.713 6122-10994/com.kwancorp.asyncapp2 D/TAG: [12916] 128
  2021-06-06 02:37:11.814 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13017] 129
  2021-06-06 02:37:11.914 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13117] 130
  2021-06-06 02:37:12.015 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13218] 131
  2021-06-06 02:37:12.116 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13319] 132
  2021-06-06 02:37:12.217 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13420] 133
  2021-06-06 02:37:12.318 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13521] 134
  2021-06-06 02:37:12.419 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13622] 135
  2021-06-06 02:37:12.520 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13723] 136
  2021-06-06 02:37:12.621 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13824] 137
  2021-06-06 02:37:12.722 6122-10994/com.kwancorp.asyncapp2 D/TAG: [13925] 138
  2021-06-06 02:37:12.823 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14025] 139
  2021-06-06 02:37:12.923 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14126] 140
  2021-06-06 02:37:13.024 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14227] 141
  2021-06-06 02:37:13.125 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14328] 142
  2021-06-06 02:37:13.226 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14429] 143
  2021-06-06 02:37:13.327 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14530] 144
  2021-06-06 02:37:13.428 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14631] 145
  2021-06-06 02:37:13.529 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14732] 146
  2021-06-06 02:37:13.631 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14834] 147
  2021-06-06 02:37:13.732 6122-10994/com.kwancorp.asyncapp2 D/TAG: [14935] 148
  2021-06-06 02:37:13.834 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15037] 149
  2021-06-06 02:37:13.935 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15138] 150
  2021-06-06 02:37:14.036 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15239] 151
  2021-06-06 02:37:14.137 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15340] 152
  2021-06-06 02:37:14.238 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15441] 153
  2021-06-06 02:37:14.339 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15542] 154
  2021-06-06 02:37:14.441 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15643] 155
  2021-06-06 02:37:14.542 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15745] 156
  2021-06-06 02:37:14.643 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15846] 157
  2021-06-06 02:37:14.745 6122-10994/com.kwancorp.asyncapp2 D/TAG: [15948] 158
  2021-06-06 02:37:14.847 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16049] 159
  2021-06-06 02:37:14.948 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16151] 160
  2021-06-06 02:37:15.050 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16252] 161
  2021-06-06 02:37:15.152 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16355] 162
  2021-06-06 02:37:15.253 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16456] 163
  2021-06-06 02:37:15.354 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16557] 164
  2021-06-06 02:37:15.455 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16658] 165
  2021-06-06 02:37:15.556 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16759] 166
  2021-06-06 02:37:15.657 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16860] 167
  2021-06-06 02:37:15.759 6122-10994/com.kwancorp.asyncapp2 D/TAG: [16961] 168
  2021-06-06 02:37:15.860 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17062] 169
  2021-06-06 02:37:15.961 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17163] 170
  2021-06-06 02:37:16.062 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17265] 171
  2021-06-06 02:37:16.164 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17366] 172
  2021-06-06 02:37:16.265 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17468] 173
  2021-06-06 02:37:16.366 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17568] 174
  2021-06-06 02:37:16.466 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17669] 175
  2021-06-06 02:37:16.567 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17770] 176
  2021-06-06 02:37:16.668 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17871] 177
  2021-06-06 02:37:16.769 6122-10994/com.kwancorp.asyncapp2 D/TAG: [17972] 178
  2021-06-06 02:37:16.870 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18073] 179
  2021-06-06 02:37:16.971 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18174] 180
  2021-06-06 02:37:17.072 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18275] 181
  2021-06-06 02:37:17.173 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18376] 182
  2021-06-06 02:37:17.274 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18477] 183
  2021-06-06 02:37:17.375 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18578] 184
  2021-06-06 02:37:17.476 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18679] 185
  2021-06-06 02:37:17.577 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18780] 186
  2021-06-06 02:37:17.679 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18881] 187
  2021-06-06 02:37:17.780 6122-10994/com.kwancorp.asyncapp2 D/TAG: [18983] 188
  2021-06-06 02:37:17.881 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19083] 189
  2021-06-06 02:37:17.982 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19185] 190
  2021-06-06 02:37:18.083 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19286] 191
  2021-06-06 02:37:18.184 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19387] 192
  2021-06-06 02:37:18.285 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19488] 193
  2021-06-06 02:37:18.386 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19589] 194
  2021-06-06 02:37:18.487 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19690] 195
  2021-06-06 02:37:18.588 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19791] 196
  2021-06-06 02:37:18.690 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19892] 197
  2021-06-06 02:37:18.790 6122-10994/com.kwancorp.asyncapp2 D/TAG: [19993] 198
  2021-06-06 02:37:18.891 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20094] 199
  2021-06-06 02:37:18.992 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20195] 200
  2021-06-06 02:37:19.094 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20297] 201
  2021-06-06 02:37:19.198 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20401] 202
  2021-06-06 02:37:19.299 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20502] 203
  2021-06-06 02:37:19.400 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20603] 204
  2021-06-06 02:37:19.503 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20705] 205
  2021-06-06 02:37:19.604 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20807] 206
  2021-06-06 02:37:19.705 6122-10994/com.kwancorp.asyncapp2 D/TAG: [20908] 207
  2021-06-06 02:37:19.806 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21009] 208
  2021-06-06 02:37:19.907 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21109] 209
  2021-06-06 02:37:20.008 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21210] 210
  2021-06-06 02:37:20.109 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21311] 211
  2021-06-06 02:37:20.210 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21412] 212
  2021-06-06 02:37:20.311 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21514] 213
  2021-06-06 02:37:20.413 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21615] 214
  2021-06-06 02:37:20.514 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21717] 215
  2021-06-06 02:37:20.615 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21818] 216
  2021-06-06 02:37:20.716 6122-10994/com.kwancorp.asyncapp2 D/TAG: [21919] 217
  2021-06-06 02:37:20.817 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22020] 218
  2021-06-06 02:37:20.918 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22121] 219
  2021-06-06 02:37:21.020 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22222] 220
  2021-06-06 02:37:21.121 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22324] 221
  2021-06-06 02:37:21.222 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22425] 222
  2021-06-06 02:37:21.323 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22526] 223
  2021-06-06 02:37:21.425 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22627] 224
  2021-06-06 02:37:21.526 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22728] 225
  2021-06-06 02:37:21.627 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22829] 226
  2021-06-06 02:37:21.729 6122-10994/com.kwancorp.asyncapp2 D/TAG: [22932] 227
  2021-06-06 02:37:21.830 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23033] 228
  2021-06-06 02:37:21.931 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23134] 229
  2021-06-06 02:37:22.033 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23235] 230
  2021-06-06 02:37:22.134 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23337] 231
  2021-06-06 02:37:22.235 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23438] 232
  2021-06-06 02:37:22.336 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23539] 233
  2021-06-06 02:37:22.438 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23640] 234
  2021-06-06 02:37:22.539 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23741] 235
  2021-06-06 02:37:22.641 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23843] 236
  2021-06-06 02:37:22.742 6122-10994/com.kwancorp.asyncapp2 D/TAG: [23945] 237
  2021-06-06 02:37:22.843 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24046] 238
  2021-06-06 02:37:22.944 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24147] 239
  2021-06-06 02:37:23.045 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24248] 240
  2021-06-06 02:37:23.147 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24349] 241
  2021-06-06 02:37:23.249 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24452] 242
  2021-06-06 02:37:23.350 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24553] 243
  2021-06-06 02:37:23.452 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24655] 244
  2021-06-06 02:37:23.553 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24756] 245
  2021-06-06 02:37:23.655 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24858] 246
  2021-06-06 02:37:23.756 6122-10994/com.kwancorp.asyncapp2 D/TAG: [24959] 247
  2021-06-06 02:37:23.857 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25060] 248
  2021-06-06 02:37:23.958 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25161] 249
  2021-06-06 02:37:24.059 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25262] 250
  2021-06-06 02:37:24.160 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25363] 251
  2021-06-06 02:37:24.261 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25463] 252
  2021-06-06 02:37:24.362 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25564] 253
  2021-06-06 02:37:24.464 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25666] 254
  2021-06-06 02:37:24.566 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25768] 255
  2021-06-06 02:37:24.667 6122-10994/com.kwancorp.asyncapp2 D/TAG: [25870] 50000000
  ```

  <br>
  </div>
  </details>
