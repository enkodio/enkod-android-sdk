# Инструкция подключения библиотеки enKod Android SDK

 > Последняя версия библиотеки com.github.enkodio:enkod-android-sdk:v1.0.3

Перед добавлением и использованием библиотеки **enkodio:androidsdk** рекомендуем ознакомиться:

- с [README Firebase Cloud Messaging](<README Firebase Cloud Messaging.md>)
- с [Additional SDK](<Additional SDK.md>)

## Инициализация библиотеки и добавления контакта

1. В файл **settings.gradle.kts** (Project Setting) в блок dependencyResolutionManagement необходимо добавить строку кода  -  `maven ("https://jitpack.io")`

2. В файл **build.gradle.kts** (Module:app) в блок dependencies необходимо добавить зависимость `com.github.enkodio:enkod-android-sdk:v1.0.3` и выполнить синхронизацию gradle

3. Для инициализации библиотеки в основном **Activity** проекта выполните метод `EnkodConnect(_account:"account").start(this)`, где:

   - `"account"` - системное имя аккаунта enKod
   - `this` - контекст

    > Данный метод должен активироваться всегда вместе с активацией приложения

4. Для работы push-уведомлений необходимо:

    > Если подключение push-уведомлений не требуется, пункт 4 следует пропустить.

   - подключить сервис Firebase Cloud Messaging к проекту
   - указать значение `true` для параметра `"_usingFcm"` в конструкторе класса EnkodConnect
     - По умолчанию `usingFcm` = false
     - Параметр `"_usingFcm"` указывается следующим после параметра `"_account"`
   - для работы с Firebase Cloud Messaging выполните `EnkodConnect(_account: "account", _usingFcm: true).start(this)`
  
5. Для регистрации контакта в сервисе enKod воспользуйтесь методом: 
   ```kotlin
   EnKodSDK.addContact(
              email:String = "",
              phone: String = "", 
              firstName: String = "", 
              lastName: String = "", 
              extraFields: Map<String, Any>? = null,
              groups: List<String>? = null
   )
   ```
   - регистрацию контакта можно произвести указав **email** и/или **phone**.
   - опционально: параметры firstName и lastName - предназначены для указания имени и фамилии
   - опционально: для указания дополнительной информации контакта можно передать в качестве параметра `"extraFields"` коллекцию `Map <String, Any>?` содержащую необходимые ключи и значения
   - опционально: параметр groups позволяет прикрепить контакт к необходимым группам рассылок. Для этого необходимо передать List<String> состоящий из системных имен групп рассылок.

Информацию о всех параметрах конструктора класса EnkodConnect (), используемых для инициализации библиотеки, можно найти в дополнительных рекомендациях по подключению библиотеки enkodio:androidsdk

## Tracking

Класс Tracking библиотеки содержит следующие публичные методы:

- `Tracking.addToCart(product: Product)` Передает на сервер информацию о добавления товара в корзину (событие "productAdd")
- `Tracking.removeFromCart(product: Product)` Передает на сервер информацию об исключении товара из корзины (событие "productRemove")
- `Tracking.addToFavourite(product: Product)` Передает на сервер информацию о добавления товара в избранное (событие "productLike")
- `Tracking.removeFromFavourite(product: Product)` Передает на сервер информацию об исключении товара из избранного (событие "productDislike")
- `Tracking.productOpen (product: Product)` Передает на сервер информацию об открытии карточки товара (событие "productOpen")\
В параметры данных методов передается data class Product:

    ```kotlin
    data class Product(
        var id: String?,
        var categoryId: String? = null,
        var count: Int? = null,
        var price: String? = null,
        var picture: String? = null,
        var params: Map <String, Any>? = null
    )
    ```

  - поле `id` - обязательное для фиксации события и должно быть указано
  - опционально: для передачи дополнительных параметров которые не подходят ни к одному из уже добавленных в класс полей можно передать в качестве параметра `"params"` коллекцию `Map <String, Any>?` содержащую необходимые ключи и значения

- `Tracking.productBuy(products: List<Order>, params: Map <String, Any>? = null, orderId: String? = null, orderDatetime: String? = null)` Передает на сервер информацию о покупке товара / товаров (событие "order")\
Метод включает следующие параметры:

  - `products: List<Order>` - данный параметр принимает список содержащий data class Order.
  - `Map <String, Any>?` - данный параметр предназначен для передачи дополнительной информации (по умолчанию null), которая относится ко всему заказу.  
  - `orderId: String?` - в данный параметр можно передать номер заказа в формате String (по умолчанию null)
    - при значении параметра равного null, метод сгенерирует случайный уникальный номер заказа в формате String и передаст его на сервер.
  - `orderDatetime: Long?` - в данный параметр можно передать время заказа в формате Long (Unix) (по умолчанию null).

    Data class Order:

    ```kotlin
    data class Order(
        var id: String?,
        var count: Int?,
        var price: String?,
        var params: Map <String, Any>? = null
    )
    ```

  - Поля `id`, `count` и `price` - обязательные для создания заказа и должны быть указаны.
    - `id: String?` - представляет собой id позиции в формате String,
    - `count: Int?` - количество единиц данной позиции добавляемых в заказ в формате Int, 
    - `price: String?` - стоимость данной позиции в формате String
  - Опционально: для передачи дополнительных параметров для конкретной позиции можно передать в качестве параметра `params:`
  коллекцию `Map <String, Any>?` содержащую необходимые ключи и значения.
