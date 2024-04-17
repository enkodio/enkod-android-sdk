# Дополнительные рекомендации Android SDK

## Добавление запроса на показ уведомлений для устройств с API LEVEL >= 33 

Для показа push уведомлений на устройствах с API LEVEL >= 33 необходимо выполнить запрос на подтверждение разрешения POST_NOTIFICATIONS у пользователей.

Для вызова разрешения можно использовать следующий метод:

```
       fun requestPermission () {

            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean -> if (isGranted) {}}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                val permissionState =

                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)

                if (permissionState == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
```
Данный метод должен быть вызван в главном activity приложения. 

Перед этим необходимо добавить разрешение в файл android manifest приложения: 

```
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

## Снятие режима энергосбережения

Для максимально эффективной работы библиотеки рекомендуется добавить в приложение запрос на снятие ограничений по энергосбережению.

Библиотека **enkodio:androidsdk** содержит метод `checkBatteryOptimization()`, который можно использовать для вызова уведомления на отмену ограничений энергосбережения.

Для этого следует активировать метод в главном **Activity** приложения. Данное разрешение может быть согласовано пользователем.

## Параметры обновления устаревшего токена

Согласно [рекомендациям](https://firebase.google.com/docs/cloud-messaging/manage-tokens?hl=en) fcm токен, не обновляемый более 2 месяцев, считается устаревшим.

В библиотеке **enkodio:androidsdk** предусмотрены функции для обновления токена.

Для их активации во время инициализации библиотеки следует:

- установить значение `true` для параметров `_tokenManualUpdate` и `_tokenAutoUpdate` (по умолчанию = true), 

- временной интервал (в часах) после которого токен fcm будет обновлен, если время с момента последнего обновления токена превышает данный временной интервал.

`_tokenManualUpdate` параметр активирует обновление во время работы приложения на переднем плане запуская сервис который выполняет данную операцию.

`_tokenAutoUpdate` параметр активирует обновление токена в тот момент когда на устройство поступают высокоприоритетные push уведомления или уведомления среднего приоритета 
на устройствах c SDK API level < 31.

> По умолчанию установлено значение true для обоих параметров.

Рекомендуемый временной интервал:

- для `_tokenManualUpdate` составляет 550 часов
- для `_tokenAutoUpdate` - 550 часов.

Данные временные интервалы установлены по умолчанию, но могут быть изменены при инициализации библиотеки.

## Полный список параметров конструктора класса EnkodConnect() применяемых во время инициализации библиотеки:

- `_account: String?` - системное имя аккаунта enKod

- `_usingFcm: Boolean?` - параметр разрешает функциям библиотеки использовать Firebase Cloud Messaging 

  > По умолчанию - false

- `_tokenManualUpdate: Boolean?` - данный параметр включает функцию обновления токена fcm во время работы приложения на переднем плане

  > По умолчанию - true

- `_tokenAutoUpdate: Boolean?` - данный параметр включает функцию обновления токена fcm во время работы приложения в фоном режиме

  > По умолчанию - true

- `_timeTokenManualUpdate: Int? = defaultTimeManualUpdateToken` - временной интервал (в часах) после которого токен fcm будет обновлен, если время с момента последнего обновления токена превышает данный временной интервал.
Обновление произойдет во время открытия приложения пользователем.

  > По умолчанию временной интервал - 550 часов

- `_timeTokenAutoUpdate: Int? = defaultTimeAutoUpdateToken` - временной интервал (в часах) после которого токен fcm будет обновлен, если время с момента последнего обновления токена превышает данный временной интервал.
Обновление произойдет в фоновом режиме.

  > По умолчанию временной интервал - 500 часов

По умолчанию конструктор класса выглядит следующем образом:

```kotlin
EnkodConnect(
  _account: "",
  _usingFcm: false,
  _tokenManualUpdate: true,
  _tokenManualUpdate: true,
  _tokenManualUpdate: 550,
  _timeTokenAutoUpdate:500
)
```
