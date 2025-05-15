# Инструкция по подключению Huawei PushKit

## Создание проекта в AppGallery Connect

1. Перейдите на сайт <https://developer.huawei.com/consumer/ru/>

2. Выберите пункт **Войти**

3. Войдите в аккаунт Huawei. Если аккаунта нет, зарегистрируйте его

4. Перейдите в  **Консоль** -> **AppGallery Connect**

5. Перейдите в **Мои проекты*

6. Нажмите  **Новый проект**. По желанию подключите **HUAWEI Analytics**

7. В открывшемся окне выберите **Добавление приложения**

8. Введите имя приложения, имя пакета (package name вашего проекта) выберите  **Android, Мобильный
   телефон, Категория: Приложение**

9. Добавьте отпечаток сертификата SHA-256 вашего проекта

## Подключение приложения к сервису PushKit

1. Скачайте agconnect-services.json и перенесите его в корневую папку вашего проекта

2. В меню слева выберите пункт **Push Kit**

3. Включите **Push Kit**

4. Добавьте в манифест проекта следующие строки (APP_ID возьмите из консоли AppGallery Connect):

>      <uses-permission android:name="com.huawei.hms.permission.SERVICE"/>
> 
> <meta-data
> android:name="com.huawei.hms.client.appid"
> android:value="APP_ID" />

5. В **settings.gradle** добавьте: 

> pluginManagement {
repositories {
...
maven ("https://developer.huawei.com/repo/")
    }
}

> dependencyResolutionManagement {
repositories {
...
        maven ("https://developer.huawei.com/repo/")
    }
}

6. В **build.gradle (app)** добавьте:

>dependencies {
...
implementation ("com.huawei.hms:push:6.13.0.300")
}
