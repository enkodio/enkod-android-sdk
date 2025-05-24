# Инструкция по подключению Firebase Cloud Messaging

## Создание проекта Firebase

1. Перейдите на сайт <https://firebase.google.com/>

2. Выберите пункт **Get started**

3. Выберите пункт **Add project**

4. Укажите название проекта, выберите **Continue**

5. Далее, по своему желанию, утвердите или откажитесь от добавления сервисов аналитики, нажмите **Continue**

6. В создавшемся проекте в меню с левой стороны страницы выберите **All products**

7. В открывшемся меню выберите **Cloud Messaging**

## Подключение приложения к сервису Firebase Cloud Messaging

1. В главном меню **Android Studio** во вкладке **Tools** выберите пункт **Firebase**

2. В открывшимся справа меню Firebase выберите пункт **Cloud Messaging**

3. В подменю Cloud Messaging нажмите на **Set up Firebase Cloud Messaging**

4. Далее после перехода в меню Set up Firebase Cloud Messaging выберите **Connect to Firebase**
Вы будете перенаправлены на страницу созданного проекта Firebase. На открывшейся странице выберите - **Connect**

    > После подключения к проекту в AndroidStudio в пункте Connect your app to Firebase отобразиться **"Connected"**

5. Далее добавьте зависимости Firebase, нажав на кнопку **Add FCM to your app**. В открывшемся меню выберите **Accept Changes**.

    > Дождитесь завершение установки зависимостей. В пункте  Add FCM to your app отобразится **"Dependencies set up correctly"**

6. Перейдите на страницу проекта Firebase. Под названием проекта вы увидите добавленное приложение. Перейдите в это приложение, нажав на **значок "настройки"**.

7. В открывшемся меню в разделе **General** найдите раздел **Your apps** в котором вы найдете добавленное приложение - нажмите на **google-services.json**.

    > Начнется загрузка файла google-services.json.

8. Скопируйте загруженный файл google-services.json и перейдите в Android Studio.

9. Вставьте скопированный файл в папку app проекта

10. Если в вашем проекте уже есть сервис, обрабатывающий push-уведомления из стороннего аккаунта Firebase,
вам необходимо положить загруженный файл в папку app/assets (при неимении нужно создать эту папку) и ОБЯЗАТЕЛЬНО переименовать его.
После этого в Application или в MainActivity необходимо вызвать метод **EnKodSDK.initSecondaryFirebaseApp(context: Context, jsonName: String)**,
передав в него applicationContext и имя json файла из папки assets. Убедитесь, что json файл соответствует следующей структуре:

>{
   "project_info": {
   "project_number": "...",
   "project_id": "...",
   "storage_bucket": "..."
   },
   "client": [
      {
         "client_info": {
         "mobilesdk_app_id": "...",
         "android_client_info": {
         "package_name": "..."
      }
   },
   "oauth_client": [],
   "api_key": [
      {
      "current_key": "..."
      }
   ],
         "services": {
            "appinvite_service": {
               "other_platform_oauth_client": []
            }
         }
      }
   ],
   "configuration_version": "..."
}

11. Чтобы обработать сообщение через библиотеку, вызовите метод **EnKodSDK.manageFcmMessageWithEnKod(
    message: RemoteMessage,
    applicationContext: Context,
    externalCall: Boolean = true,
    )**

12. Чтобы получить токен от второго аккаунта Firebase, вызовите метод **EnKodSDK.getFcmTokenFromCustomApp()**

> Приложение готово к работе с Firebase Cloud Messaging

## Добавление service accout key в профиль enKod (настройки рассылки)

1. Перейдите на сайт проекта **Firebase**

2. Откройте **Project Settings**

3. Перейдите в раздел **Service account**

4. Во вкладке **Firebase Admin SDK** выберите **Go**

5. Нажмите на **Generate new private key**

6. В открывшемся окне выберете **Generate key** - после нажатия будет загружен **service accout json**

7. Перейдите на страницу вашего аккаунта enKod

8. В нижнем правом углу страницы выберите **"Настройки"**

9. В открывшимся меню выберите **"Рассылки"**

10. В разделе "Рассылки" выберите **"Мобильное приложение"**

11. В разделе "Мобильное приложение" укажите название приложения, в качестве способа подключения выберите **Firebase Cloud Messaging**.

12. В поле Firebase Service Account Key загрузите **service accout json** загруженный с сайта Firebase

> Сервис готов к работе с Firebase Cloud Messaging
