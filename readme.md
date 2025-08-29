# الگو‌های طراحی و بازآرایی کد

## فاز اول - سیستم مدیریت پویای مصرف انرژی هوشمند در یک ساختمان

در این فاز قرار است با رویکرد
`TDD`
پروژه را پیاده‌سازی کنیم.

تست‌ها را داخل پوشه مربوطه می‌سازیم:

```java
public class TariffStrategyTest {

    @Test
    void standardTariff_cost_is500PerUnit() {
        TariffStrategy t = new StandardTariff();
        assertEquals(5000L, t.cost(10));
    }

    @Test
    void peakTariff_cost_is1000PerUnit() {
        TariffStrategy t = new PeakTariff();
        assertEquals(3000L, t.cost(3));
    }

    @Test
    void greenTariff_cost_is300PerUnit() {
        TariffStrategy t = new GreenTariff();
        assertEquals(3000L, t.cost(10));
    }

    @Test
    void standard_is_500_per_unit() {
        TariffStrategy t = new StandardTariff();
        assertEquals(500L, t.cost(1));
        assertEquals(5000L, t.cost(10));
        assertEquals("Standard", t.name());
    }

    @Test
    void peak_is_1000_per_unit() {
        TariffStrategy t = new PeakTariff();
        assertEquals(1000L, t.cost(1));
        assertEquals(7000L, t.cost(7));
        assertEquals("Peak Hours", t.name());
    }

    @Test
    void green_is_300_per_unit_and_handles_zero() {
        TariffStrategy t = new GreenTariff();
        assertEquals(0L, t.cost(0));
        assertEquals(3000L, t.cost(10));
        assertEquals("Green Mode", t.name());
    }
}
```

```java
public class EnergyStateTest {

    @Test
    void active_multiplier_is1() {
        EnergyState s = new ActiveState();
        assertEquals(1.0, s.consumptionMultiplier(), 1e-9);
    }

    @Test
    void eco_multiplier_is0_5() {
        EnergyState s = new EcoState();
        assertEquals(0.5, s.consumptionMultiplier(), 1e-9);
    }

    @Test
    void shutdown_multiplier_is0() {
        EnergyState s = new ShutdownState();
        assertEquals(0.0, s.consumptionMultiplier(), 1e-9);
    }


    @Test
    void active_has_name_and_message() {
        EnergyState s = new ActiveState();
        assertEquals("Active", s.name());
        assertNotNull(s.enterMessage());
        assertTrue(s.enterMessage().contains("Active"));
    }

    @Test
    void eco_has_name_and_message() {
        EnergyState s = new EcoState();
        assertEquals("Eco Mode", s.name());
        assertNotNull(s.enterMessage());
        assertTrue(s.enterMessage().contains("Eco"));
    }

    @Test
    void shutdown_has_name_and_message() {
        EnergyState s = new ShutdownState();
        assertEquals("Shutdown", s.name());
        assertNotNull(s.enterMessage());
        assertTrue(s.enterMessage().contains("Shutdown"));
    }

    @Test
    void multipliers_within_0_and_1_and_ordered() {
        EnergyState active = new ActiveState();
        EnergyState eco = new EcoState();
        EnergyState shutdown = new ShutdownState();

        double a = active.consumptionMultiplier();
        double e = eco.consumptionMultiplier();
        double z = shutdown.consumptionMultiplier();

        assertTrue(0.0 <= a && a <= 1.0);
        assertTrue(0.0 <= e && e <= 1.0);
        assertTrue(0.0 <= z && z <= 1.0);

        assertTrue(a >= e, "Active باید >= Eco باشد");
        assertTrue(e >= z, "Eco باید >= Shutdown باشد");
    }
}
```

```java
public class SmartBuildingSystemTest {

    @Test
    void active_standard_10units_cost_5000() {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        assertEquals(5000L, sys.simulateCost(10));
        assertEquals(10L, sys.adjustedUnits(10));
    }

    @Test
    void eco_green_10units_cost_1500() {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        sys.setState(new EcoState());
        sys.setTariff(new GreenTariff());
        assertEquals(5L, sys.adjustedUnits(10));
        assertEquals(1500L, sys.simulateCost(10));
    }

    @Test
    void shutdown_anyTariff_cost_zero() {
        SmartBuildingSystem sys = SmartBuildingSystem.defaultSystem();
        sys.setState(new ShutdownState());
        assertEquals(0L, sys.adjustedUnits(100));
        assertEquals(0L, sys.simulateCost(100));
    }
}
```

```java
public class EnergyControllerTest {

    @Test
    void initial_state_reflected_in_status_and_multiplier() {
        EnergyController controller = new EnergyController(new ActiveState());
        assertEquals("Active", controller.status());
        assertEquals(1.0, controller.multiplier(), 1e-9);
    }

    @Test
    void changing_to_eco_updates_status_and_multiplier() {
        EnergyController controller = new EnergyController(new ActiveState());
        controller.setState(new EcoState());
        assertEquals("Eco Mode", controller.status());
        assertEquals(0.5, controller.multiplier(), 1e-9);
    }

    @Test
    void changing_to_shutdown_updates_status_and_multiplier() {
        EnergyController controller = new EnergyController(new ActiveState());
        controller.setState(new ShutdownState());
        assertEquals("Shutdown", controller.status());
        assertEquals(0.0, controller.multiplier(), 1e-9);
    }
}
```

در ابتدا که تست‌ها را اجرا می‌کنیم هیچ کدام پاس نمی‌شوند:

![](figs/image.png)

![](figs/image-1.png)

حال رفته‌رفته پروژه را پیاده‌سازی می‌کنیم تا تست‌ها پاس شوند.

ساختار پروژه:

```
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── edu
    │           └── sharif
    │               └── selab
    │                   ├── App.java
    │                   ├── core
    │                   │   ├── BillingService.java
    │                   │   ├── EnergyController.java
    │                   │   └── SmartBuildingSystem.java
    │                   ├── state
    │                   │   ├── EnergyState.java
    │                   │   ├── ActiveState.java
    │                   │   ├── EcoState.java
    │                   │   └── ShutdownState.java
    │                   └── strategy
    │                       ├── TariffStrategy.java
    │                       ├── StandardTariff.java
    │                       ├── PeakTariff.java
    │                       └── GreenTariff.java
    └── test
        └── java
            └── edu
                └── sharif
                    └── selab
                        ├── EnergyStateTest.java
                        ├── EnergyStateNamingAndMessageTest.java
                        ├── EnergyStateInvariantTest.java
                        ├── EnergyControllerTest.java
                        ├── SmartBuildingSystemIntegrationTest.java
                        └── TariffStrategyTest.java
```

### توضیحات هر بخش:

- Main.java → نقطه‌ی شروع (منوی کنسولی)
- core/ → بخش Context (مدیریت state + strategy)

- state/ → پیاده‌سازی الگوی State (Active, Eco, Shutdown)

- strategy/ → پیاده‌سازی الگوی Strategy (Standard, Peak, Green)

- test/ → تست‌های JUnit5 برای هر بخش، مطابق TDD

- pom.xml → مدیریت وابستگی‌ها (JUnit, plugins, ...)

### توضیحات الگوی طراحی

در این پروژه ما دو الگوی طراحی اصلی داریم: `Strategy` و `State`. هر کدام هدف و کاربرد متفاوتی دارند ولی با هم ترکیب می‌شوند تا سیستم انعطاف‌پذیر و قابل توسعه باشد.

#### الگوی Strategy

الگوی `Strategy` برای جداسازی منطق محاسبه هزینه‌ی انرژی از بقیه‌ی سیستم استفاده شده است. در این پروژه، کلاس‌های مختلفی مثل `StandardTariff`, `PeakTariff`, و `GreenTariff` همگی یک اینترفیس مشترک به نام `TariffStrategy` را پیاده‌سازی می‌کنند. این کار باعث می‌شود که نحوه‌ی محاسبه‌ی هزینه (هر واحد ۵۰۰، ۱۰۰۰ یا ۳۰۰ تومان) به‌صورت قابل تعویض در زمان اجرا باشد. یعنی بدون تغییر در منطق اصلی سیستم، مدیر ساختمان می‌تواند سیاست تعرفه را تغییر دهد. مزیت این الگو این است که اگر در آینده تعرفه‌های جدیدی اضافه شوند (مثلاً «تعرفه آخر هفته»)، کافی است یک کلاس جدید ایجاد شود، بدون آنکه نیاز به تغییر در سایر بخش‌ها باشد.

#### الگوی State

الگوی State برای مدیریت وضعیت سیستم انرژی ساختمان به کار رفته است. سیستم می‌تواند در حالت‌های مختلفی باشد: `ActiveState`, `EcoState`, و `ShutdownState`. هر حالت رفتار خاص خودش را دارد (مثلاً ضریب مصرف ۱، ۰.۵ یا ۰) و پیام متفاوتی هنگام فعال شدن چاپ می‌کند. به‌جای نوشتن شرط‌های پیچیده (`if/else` یا `switch`) در کد اصلی، هر حالت به‌صورت یک کلاس مستقل پیاده‌سازی شده و منطق مربوط به خودش را دارد. این باعث می‌شود تغییر یا اضافه کردن حالت جدید خیلی ساده‌تر باشد و کد اصلی تمیز و قابل نگهداری باقی بماند.

#### پیاده‌سازی State

اینترفیس مربوط به این بخش به این صورت است:

```java
public interface EnergyState {
    String name();

    double consumptionMultiplier();

    String enterMessage();
}
```

حالت‌هایی که این اینترفیس implement می‌شود:

وضعیت فعال:

```java
public final class ActiveState implements EnergyState {
    @Override
    public String name() {
        return "Active";
    }

    @Override
    public double consumptionMultiplier() {
        return 1.0;
    }

    @Override
    public String enterMessage() {
        return "Active state: all systems are running.";
    }
}
```

وضعیت Eco:

```java
public final class EcoState implements EnergyState {
    @Override
    public String name() {
        return "Eco Mode";
    }

    @Override
    public double consumptionMultiplier() {
        return 0.5;
    }

    @Override
    public String enterMessage() {
        return "Eco state: only essential systems are running.";
    }

}
```

وضعیت خاموش:

```java
public final class ShutdownState implements EnergyState {
    @Override
    public String name() {
        return "Shutdown";
    }

    @Override
    public double consumptionMultiplier() {
        return 0.0;
    }

    @Override
    public String enterMessage() {
        return "Shutdown state: all systems are off.";
    }
}
```

#### پیاده‌سازی Strategy

اینترفیس مربوط به این بخش:

```java
public interface TariffStrategy {
    long cost(long units);

    String name();
}
```

و پیاده‌سازی‌های آن:

```java
public final class StandardTariff implements TariffStrategy {
    @Override
    public long cost(long units) {
        return units * 500L;
    }

    @Override
    public String name() {
        return "Standard";
    }
}

```

```java
public final class PeakTariff implements TariffStrategy {
    @Override
    public long cost(long units) {
        return units * 1000L;
    }

    @Override
    public String name() {
        return "Peak Hours";
    }
}
```

```java
public final class GreenTariff implements TariffStrategy {
    @Override
    public long cost(long units) {
        return units * 300L;
    }

    @Override
    public String name() {
        return "Green Mode";
    }
}
```

سپس Menu ها و ماژول
Core
را هم پیاده‌سازی می‌کنیم که داخل سورس پروژه موجود است.

حال تست‌ها را با دستور زیر اجرا می‌کنیم:

```
maven test
```

می‌بینیم که همه‌ی تست‌های ما به درستی پاس شدند و برنامه درست کار می‌کند:

![](figs/image-2.png)

مستندات اجرای صحیح پروژه:

![](figs/image-3.png)

![](figs/image-4.png)

![](figs/image-5.png)

![](figs/image-6.png)

---

---

---

## فاز دوم - پیاده‌سازی چندین بازآیی و نوشتن گزارش

### الگوی Facade – سناریو اول

در این بازآرایی، الگو را روی کلاس `Parser` پیاده‌سازی کردیم. برای این منظور یک کلاس جدید به نام `ParserFacade` ایجاد شد. مدیریت استثناها که قبلاً در `Main` انجام می‌شد، به داخل این کلاس منتقل گردید. در ادامه، `Main` هم متناسب با این تغییر ساده‌تر شد.

### الگوی Facade – سناریو دوم

اینجا الگو را روی کلاس `CodeGenerator` اعمال نمودیم. برای این کار کلاس کمکی `CodeGeneratorFacade` اضافه شد. از آنجا که تنها متدی که بیرون استفاده می‌شد `semanticFunction` بود، همین متد در `CodeGeneratorFacade` قرار گرفت. همچنین چون این متد فقط در `Parser` فراخوانی می‌شد، کد آن قسمت هم بر اساس ساختار تازه اصلاح گردید. علاوه بر آن، مدیریت خطاها که در `Parser` بود، اکنون در `CodeGeneratorFacade` انجام می‌شود.

### الگوی Replace Type Code with State/Strategy

این تغییر در کلاس `Address` انجام شد. ابتدا `TypeAddress` که قبلاً به صورت یک enum وجود داشت حذف گردید و به جای آن یک کلاس انتزاعی با همان نام ایجاد شد. در این کلاس متد `toString` به‌صورت abstract تعریف شد. سپس سه کلاس `Direct`، `Indirect` و `Imidiate` پیاده‌سازی شدند که از `TypeAddress` ارث‌بری می‌کنند و هرکدام نسخه‌ی خاص خود از متد `toString` را ارائه می‌دهند. در نهایت در `Address` تنها کافی است `toString` روی شیء `Type` فراخوانی شود. همچنین در کلاس `CodeGenerator` تغییراتی اعمال شد تا نمونه‌سازی این کلاس‌های جدید به‌جای استفاده از enum انجام گیرد.

### الگوی Separate Query from Modifier

در کلاس `Memory` این بازآرایی انجام گرفت. متد `saveMemory` که همزمان مسئول ثبت و خواندن بود، به دو متد مستقل تقسیم شد: `addMemory` برای ذخیره و `getMemorySize` برای خواندن اندازه. در تمام قسمت‌هایی که قبلاً `saveMemory` فراخوانی می‌شد، حالا از این دو متد تازه استفاده می‌کنیم تا نقش نوشتن و خواندن تفکیک شود.

### الگوی Self Encapsulate Field

این تغییر در کلاس `Parser` روی متغیر `rules` صورت گرفت. برای این متغیر دو متد `getRules` و `setRules` ساخته شد و دسترسی مستقیم به `rules` حذف گردید. از این به بعد در سرتاسر کد به جای ارجاع مستقیم، از متدهای ذکرشده استفاده می‌کنیم.

### الگوی انتخابی اول: Parameterize Method

این بازآرایی در `CodeGenerator` به کار رفت. بررسی‌ها نشان داد که سه متد `add`، `sub` و `mult` منطق بسیار مشابهی دارند. بنابراین یک متد عمومی به نام `basicOperations` تعریف شد و نوع عملیات موردنظر از طریق پارامتر `operation` به آن ارسال می‌شود.

### الگوی انتخابی دوم: Encapsulate Field

برای این مورد، کلاس `Action` انتخاب شد. سطح دسترسی فیلد `action` به `private` تغییر یافت و یک متد getter برای آن نوشته شد. سپس در `Parser`، به جای دسترسی مستقیم به متغیر، از این getter استفاده کردیم تا همخوانی با اصل کپسوله‌سازی برقرار شود.

---

---

---

## پاسخ به سوالات انتهایی دستورکار

### سوال اول | در کتاب GoF سه دسته الگوی طراحی معرفی شده است. آنها را نام ببرید و در مورد هر دسته در حد دو خط توضیح دهید و برای هر دسته دو نمونه الگو متعلق به آن دسته را نام ببرید.

### ۱. الگوهای ایجادی (Creational Patterns)

این دسته از الگوها به فرآیند ایجاد اشیاء (objects) مربوط می‌شوند و تلاش می‌کنند تا پیچیدگی‌های ساخت اشیاء را کاهش داده و فرآیند آن را ساده‌تر و انعطاف‌پذیرتر کنند. این الگوها با پنهان کردن منطق ایجاد اشیاء، به سیستم استقلال بیشتری در مورد نحوه ایجاد، ترکیب و نمایش اشیاء می‌بخشند.

**مثال‌ها:**

- **Singleton (تک نمونه):** این الگو تضمین می‌کند که از یک کلاس تنها یک نمونه (instance) ساخته شود و یک نقطه دسترسی سراسری برای آن فراهم می‌کند.
- **Factory Method (متد کارخانه‌ای):** این الگو یک رابط برای ایجاد اشیاء در یک ابرکلاس (superclass) تعریف می‌کند، اما به زیرکلاس‌ها (subclasses) اجازه می‌دهد تا نوع شیءای که ایجاد می‌شود را تغییر دهند.

### ۲. الگوهای ساختاری (Structural Patterns)

الگوهای ساختاری با نحوه ترکیب کلاس‌ها و اشیاء برای تشکیل ساختارهای بزرگتر سروکار دارند. این الگوها با شناسایی روابط ساده بین موجودیت‌ها، به ساده‌سازی ساختار و افزایش کارایی و انعطاف‌پذیری آن کمک می‌کنند.

**مثال‌ها:**

- **Adapter (آداپتور):** این الگو به اشیائی با رابط‌های ناسازگار اجازه می‌دهد تا با یکدیگر همکاری کنند.
- **Decorator (تزئین‌گر):** این الگو به شما امکان می‌دهد تا با قرار دادن اشیاء در داخل کلاس‌های بسته‌بندی خاص، قابلیت‌های جدیدی را به صورت پویا به آن‌ها اضافه کنید.

### ۳. الگوهای رفتاری (Behavioral Patterns)

این دسته از الگوها بر روی الگوریتم‌ها و تخصیص مسئولیت‌ها بین اشیاء تمرکز دارند. آن‌ها نه تنها الگوهای ارتباطی بین اشیاء را توصیف می‌کنند، بلکه نحوه توزیع مسئولیت‌ها و کنترل جریان‌های پیچیده را نیز مدیریت می‌کنند.

**مثال‌ها:**

- **Observer (ناظر):** این الگو یک مکانیزم اشتراک تعریف می‌کند که به چندین شیء اجازه می‌دهد تا در مورد هر رویدادی که برای شیء مورد مشاهده آن‌ها رخ می‌دهد، مطلع شوند.
- **Strategy (استراتژی):** این الگو به شما اجازه می‌دهد تا خانواده‌ای از الگوریتم‌ها را تعریف کرده، هر یک را در یک کلاس جداگانه قرار دهید و اشیاء آن‌ها را قابل تعویض کنید.

---

### سوال دوم | الگوهای استفاده شده در فاز اول آزمایش جزو کدام دسته الگوی طراحی هستند؟

#### الگوهای Strategy و State در دسته الگوهای رفتاری (Behavioral Patterns) قرار می‌گیرند.

هر دوی این الگوها به نحوه تعامل و ارتباط بین اشیاء می‌پردازند و هدفشان افزایش انعطاف‌پذیری در نحوه انجام عملیات در زمان اجرا است.

- **الگوی Strategy (استراتژی):** این الگو به شما اجازه می‌دهد تا خانواده‌ای از الگوریتم‌ها را تعریف کرده، هر یک را در یک کلاس جداگانه قرار دهید و آن‌ها را در زمان اجرا قابل تعویض کنید. به عبارت دیگر، الگوی استراتژی بر روی "چگونگی" انجام یک کار تمرکز دارد.

- **الگوی State (وضعیت):** این الگو به یک شیء اجازه می‌دهد تا رفتار خود را با تغییر وضعیت داخلی‌اش تغییر دهد. این الگو بر روی "چیستی" وضعیت یک شیء و رفتار متناسب با آن وضعیت تمرکز می‌کند.

اگرچه ساختار این دو الگو بسیار شبیه به هم است، اما هدف و کاربرد آن‌ها متفاوت است. الگوی Strategy معمولاً توسط کلاینت (client) برای انتخاب یک الگوریتم مشخص به کار می‌رود، در حالی که در الگوی State، خود شیء (context) وضعیت داخلی خود را مدیریت کرده و رفتار خود را بر اساس آن تغییر می‌دهد.

---

### سوال سوم | با توجه به اینکه در سیستم مدیریت پویای مصرف انرژی هوشمند در یک ساختمان اداری، در هر زمان سیستم دقیقاً در یکی از سه حالت `Active`، `Eco Mode` یا `Shutdown` قرار دارد و سیاست محاسبه هزینه نیز می‌تواند بین تعرفه‌های `Standard`، `Peak Hours` یا `Green Mode` تغییر کند، کدام الگوی طراحی برای مدیریت این تغییرات حالت و سیاست‌ها مناسب‌تر است؟ ضمن بیان دلایل انتخاب الگوی طراحی، نحوه پیاده‌سازی آن را با توجه به مشخصات سیستم (شامل تغییر وضعیت سیستم، تغییر سیاست محاسبه هزینه، مشاهده وضعیت و محاسبه هزینه) به طور کامل توضیح دهید.

با توجه به مشخصات سیستم مدیریت هوشمند انرژی، بهترین رویکرد استفاده از **ترکیب دو الگوی طراحی State و Strategy** است. این دو الگو هر کدام بخشی از نیازمندی‌های سیستم را به بهترین شکل پوشش می‌دهند و با همکاری یکدیگر، یک راه‌حل انعطاف‌پذیر و قابل توسعه ایجاد می‌کنند.

در ادامه دلایل انتخاب و نحوه پیاده‌سازی کامل آن شرح داده می‌شود.

### دلایل انتخاب الگوهای طراحی

#### ۱. الگوی وضعیت (State Pattern) برای مدیریت وضعیت سیستم

سیستم در هر لحظه دقیقاً در یکی از سه حالت `Active`، `Eco Mode` یا `Shutdown` قرار دارد. رفتار کلی سیستم، مانند میزان مصرف انرژی، قوانین عملکردی و انتقال به وضعیت‌های دیگر، به شدت به حالت فعلی آن وابسته است.

**دلایل انتخاب:**

- **کپسوله‌سازی رفتار وابسته به وضعیت:** به جای استفاده از دستورات شرطی طولانی و تو در تو (if/else یا switch) در کلاس اصلی برای مدیریت رفتار در هر وضعیت، الگوی State این منطق را به کلاس‌های مجزا برای هر وضعیت منتقل می‌کند. این کار کد را تمیزتر، خواناتر و قابل مدیریت‌تر می‌کند.
- **پایبندی به اصل Open/Closed:** اگر در آینده وضعیت جدیدی مانند `Maintenance Mode` به سیستم اضافه شود، کافی است یک کلاس وضعیت جدید ایجاد کنیم بدون اینکه نیازی به تغییر کد کلاس اصلی سیستم (Context) یا وضعیت‌های دیگر باشد.
- **مدیریت آسان انتقال وضعیت‌ها:** هر کلاس وضعیت می‌تواند مسئولیت انتقال به وضعیت بعدی را بر عهده بگیرد. برای مثال، وضعیت `Active` می‌تواند بر اساس یک رویداد (مانند عدم حضور افراد) تصمیم بگیرد که سیستم را به وضعیت `Eco Mode` منتقل کند.

#### ۲. الگوی استراتژی (Strategy Pattern) برای مدیریت سیاست محاسبه هزینه

سیاست محاسبه هزینه (`Standard`، `Peak Hours` یا `Green Mode`) یک الگوریتم است که می‌تواند مستقل از وضعیت فعلی سیستم تغییر کند. برای مثال، سیستم می‌تواند در وضعیت `Active` باشد و هزینه آن بر اساس تعرفه `Peak Hours` یا `Standard` محاسبه شود.

**دلایل انتخاب:**

- **کپسوله‌سازی الگوریتم‌ها:** الگوی استراتژی به شما اجازه می‌دهد تا هر یک از الگوریتم‌های محاسبه هزینه را در کلاس جداگانه‌ای قرار دهید.
- **قابلیت تعویض در زمان اجرا:** این الگو به سیستم اجازه می‌دهد تا سیاست محاسبه هزینه را به صورت پویا و در زمان اجرا تغییر دهد، بدون اینکه نیازی به تغییر در کلاسی باشد که از این سیاست استفاده می‌کند.
- **جداسازی دغدغه‌ها (Separation of Concerns):** منطق مدیریت وضعیت سیستم از منطق محاسبه هزینه کاملاً جدا می‌شود. کلاس سیستم اصلی نگران "چگونگی" محاسبه هزینه نیست، بلکه فقط این وظیفه را به شیء استراتژی فعلی واگذار می‌کند.

### نحوه پیاده‌سازی کامل

در اینجا یک طرح کلی از نحوه پیاده‌سازی این سیستم با استفاده از ترکیب دو الگو ارائه می‌شود.

#### گام اول: تعریف رابط‌ها (Interfaces)

```csharp
public interface ISystemState
{
    void EnterState(EnergyManagementSystem system);
    void Execute(EnergyManagementSystem system);
    string GetStateName();
}

public interface ICostCalculationStrategy
{
    double CalculateCost(double energyConsumed);
    string GetPolicyName();
}
```

این بخش، قراردادهای (interfaces) اصلی را تعریف می‌کند. `ISystemState` متدهایی را مشخص می‌کند که هر کلاس وضعیت باید پیاده‌سازی کند (مانند ورود به وضعیت و اجرای رفتار آن) و `ICostCalculationStrategy` رابط مربوط به الگوریتم‌های محاسبه هزینه را تعریف می‌کند.

#### گام دوم: پیاده‌سازی کلاس‌های وضعیت (Concrete States)

```csharp
public class ActiveState : ISystemState
{
    public void EnterState(EnergyManagementSystem system) { }
    public void Execute(EnergyManagementSystem system) { }
    public string GetStateName() => "Active";
}

public class EcoModeState : ISystemState
{
    public void EnterState(EnergyManagementSystem system) { }
    public void Execute(EnergyManagementSystem system) { }
    public string GetStateName() => "Eco Mode";
}

public class ShutdownState : ISystemState
{
    public void EnterState(EnergyManagementSystem system) { }
    public void Execute(EnergyManagementSystem system) { }
    public string GetStateName() => "Shutdown";
}
```

در این قسمت، کلاس‌های مربوط به هر یک از وضعیت‌های سیستم (`Active`، `Eco Mode` و `Shutdown`) پیاده‌سازی شده‌اند. هر کلاس مسئول تعریف رفتار خاص سیستم در آن وضعیت مشخص است.

#### گام سوم: پیاده‌سازی کلاس‌های استراتژی (Concrete Strategies)

```csharp
public class StandardTariffStrategy : ICostCalculationStrategy
{
    public double CalculateCost(double energyConsumed) => energyConsumed * 0.15;
    public string GetPolicyName() => "Standard";
}

public class PeakHoursTariffStrategy : ICostCalculationStrategy
{
    public double CalculateCost(double energyConsumed) => energyConsumed * 0.25;
    public string GetPolicyName() => "Peak Hours";
}

public class GreenModeTariffStrategy : ICostCalculationStrategy
{
    public double CalculateCost(double energyConsumed) => energyConsumed * 0.12;
    public string GetPolicyName() => "Green Mode";
}
```

این کدها کلاس‌های مربوط به سیاست‌های مختلف محاسبه هزینه را پیاده‌سازی می‌کنند. هر کلاس (`StandardTariffStrategy`، `PeakHoursTariffStrategy` و `GreenModeTariffStrategy`) شامل منطق محاسباتی منحصر به فرد خود است.

#### گام چهارم: ایجاد کلاس اصلی سیستم (Context Class)

```csharp
public class EnergyManagementSystem
{
    private ISystemState _currentState;
    private ICostCalculationStrategy _currentCostStrategy;

    public EnergyManagementSystem()
    {
        _currentState = new ShutdownState();
        _currentCostStrategy = new StandardTariffStrategy();
    }

    public void ChangeState(ISystemState newState)
    {
        _currentState = newState;
        _currentState.EnterState(this);
    }

    public void SetCostCalculationPolicy(ICostCalculationStrategy newStrategy)
    {
        _currentCostStrategy = newStrategy;
    }

    public string GetCurrentStateName() => _currentState.GetStateName();
    public string GetCurrentPolicyName() => _currentCostStrategy.GetPolicyName();

    public void PerformOperations()
    {
        _currentState.Execute(this);
    }

    public double GetCurrentCost(double energyConsumed)
    {
        return _currentCostStrategy.CalculateCost(energyConsumed);
    }
}
```

این کلاس اصلی سیستم (Context) است که وضعیت و استراتژی فعلی را نگهداری می‌کند. این کلاس متدهایی برای تغییر وضعیت (`ChangeState`)، تغییر سیاست هزینه (`SetCostCalculationPolicy`)، اجرای عملیات متناسب با وضعیت فعلی (`PerformOperations`) و محاسبه هزینه بر اساس استراتژی فعلی (`GetCurrentCost`) فراهم می‌کند.

### جمع‌بندی نحوه عملکرد

- **تغییر وضعیت سیستم:** برای تغییر وضعیت، متد `ChangeState` از کلاس `EnergyManagementSystem` را با یک نمونه از وضعیت جدید (مثلاً `new ActiveState()`) فراخوانی می‌کنیم. این کار باعث می‌شود رفتار سیستم در فراخوانی‌های بعدی متد `PerformOperations` تغییر کند.
- **تغییر سیاست هزینه:** برای تغییر تعرفه، متد `SetCostCalculationPolicy` را با یک نمونه از استراتژی جدید (مثلاً `new PeakHoursTariffStrategy()`) فراخوانی می‌کنیم. این تغییر بلافاصله در محاسبات بعدی هزینه از طریق متد `GetCurrentCost` اعمال می‌شود.
- **مشاهده وضعیت:** با فراخوانی متدهای `GetCurrentStateName` و `GetCurrentPolicyName` می‌توان به راحتی وضعیت و سیاست فعلی را برای نمایش در داشبورد یا گزارش‌گیری مشاهده کرد.
- **محاسبه هزینه:** کلاس `EnergyManagementSystem` مسئولیت محاسبه هزینه را به طور کامل به شیء استراتژی فعلی خود **واگذار (Delegate)** می‌کند. این جداسازی، هسته اصلی الگوی Strategy است.

---

### سوال چهارم | تحقق و یا عدم تحقق هر کدام از اصول `SOLID` را در خصوص الگوی طراحی `Factory Method` بیان کنید (هر کدام حداکثر در سه خط)

#### ۱. اصل مسئولیت واحد (Single-Responsibility Principle - SRP)

**بله، این اصل رعایت می‌شود.**
این الگو مسئولیت ایجاد اشیاء را از کلاس اصلی (Creator) به زیرکلاس‌هایش (Concrete Creators) منتقل می‌کند. به این ترتیب، کلاس اصلی تنها یک دلیل برای تغییر دارد (منطق کسب‌وکار) و هر زیرکلاس نیز دلیل مجزای خود را برای تغییر دارد (ایجاد یک محصول خاص).

#### ۲. اصل باز/بسته (Open/Closed Principle - OCP)

**بله، این اصل به طور کامل پشتیبانی می‌شود.**
برای افزودن یک محصول جدید، نیازی به تغییر کد موجود در کلاس Creator یا کدی که از آن استفاده می‌کند نیست. تنها کافی است یک کلاس محصول جدید و یک کلاس سازنده (Creator) جدید برای آن ایجاد کنیم و به این ترتیب سیستم برای توسعه باز و برای تغییر بسته است.

#### ۳. اصل جایگزینی لیسکوف (Liskov Substitution Principle - LSP)

**بله، این الگو بر پایه این اصل کار می‌کند.**
کد کلاینت با رابط والد (Creator) کار می‌کند و انتظار یک محصول با رابط `Product` را دارد. از آنجایی که تمام محصولات واقعی (Concrete Products) از این رابط پیروی می‌کنند، می‌توان آن‌ها را بدون مشکل جایگزین یکدیگر کرد و عملکرد صحیح برنامه حفظ می‌شود.

#### ۴. اصل تفکیک رابط (Interface Segregation Principle - ISP)

**بله، این اصل رعایت می‌شود.**
الگوی متد کارخانه‌ای به خودی خود باعث نقض این اصل نمی‌شود. این الگو بر یک رابط محصول (`Product`) تکیه دارد و اگر این رابط به درستی و به صورت تفکیک‌شده طراحی شده باشد، اصل ISP نیز به طور کامل رعایت خواهد شد.

#### ۵. اصل وارونگی وابستگی (Dependency Inversion Principle - DIP)

**بله، این اصل یکی از اهداف اصلی این الگو است.**
کلاس سطح بالا (Creator) به ماژول‌های سطح پایین (Concrete Products) وابسته نیست. در عوض، هر دو به یک انتزاع (Abstraction) که همان رابط `Product` است، وابسته‌اند. این الگو وابستگی‌ها را معکوس کرده و اتصال سست (Loose Coupling) را ترویج می‌دهد.

---

### سوال پنجم | هر یک از مفاهیم (کد تمیز، بدهی فنی، بوی بد ) را در حد یک خط توضیح دهید.

- **کد تمیز (Clean Code):** کدی است که به سادگی قابل خواندن، درک، تغییر و نگهداری توسط هر توسعه‌دهنده‌ای باشد.
- **بدهی فنی (Technical Debt):** هزینه بلندمدت انتخاب راه‌حل‌های آسان و سریع به جای راه‌حل‌های اصولی و بهینه در توسعه نرم‌افزار است.
- **بوی بد کد (Bad Smell):** نشانه‌ای در کد است که به وجود یک مشکل عمیق‌تر در طراحی یا پیاده‌سازی اشاره می‌کند، حتی اگر کد در ظاهر به درستی کار کند.

---

### سوال ششم | طبق دسته‌بندی وبسایت [refactoring.guru](https://refactoring.guru/refactoring/smells)، بوهای بد کد به پنج دسته تقسیم میشوند. در مورد هر کدام از این پنج دسته توضیح مختصری دهید.

#### ۱. متورم‌ها (Bloaters)

این دسته به کد، متدها و کلاس‌هایی اشاره دارد که به قدری بزرگ و حجیم شده‌اند که کار با آن‌ها دشوار است. این بوها معمولاً به مرور زمان و با توسعه برنامه، انباشته می‌شوند.

#### ۲. سوءاستفاده‌کنندگان از شیءگرایی (Object-Orientation Abusers)

این بوها زمانی به وجود می‌آیند که اصول برنامه‌نویسی شیءگرا به صورت ناقص یا نادرست به کار گرفته شوند. مانند استفاده نادرست از وراثت یا پیاده‌سازی نکردن صحیح الگوهای شیءگرا.

#### ۳. بازدارنده‌های تغییر (Change Preventers)

این دسته از بوها باعث می‌شوند که برای ایجاد یک تغییر در بخشی از کد، مجبور به اعمال تغییرات متعدد در بخش‌های دیگر شوید. این وضعیت، فرآیند توسعه را پیچیده و پرهزینه می‌کند.

#### ۴. موارد قابل حذف (Dispensables)

این بوها شامل موارد بی‌فایده و غیرضروری در کد هستند که حذف آن‌ها باعث تمیزتر، بهینه‌تر و قابل فهم‌تر شدن کد می‌شود. مانند کدهای تکراری، کامنت‌های غیرضروری یا کلاس‌هایی که کاربرد چندانی ندارند.

#### ۵. جفت‌کننده‌ها (Couplers)

تمام بوهای موجود در این گروه به وابستگی (Coupling) بیش از حد بین کلاس‌ها منجر می‌شوند یا نشان‌دهنده جایگزینی وابستگی با تفویض اختیار (Delegation) بیش از حد هستند که پیچیدگی ناخواسته ایجاد می‌کند.

---

### سوال هفتم | یکی از انواع بوهای بد، `Feature Envy` است. این بوی بد در کدام یک از دسته بندی های پنجگانه قرار می گیرد؟ برای برطرف کردن این بو، استفاده از کدام بازآرایی ها پیشنهاد می شود؟ در چه مواقعی باید این بو را نادیده گرفت؟

#### ۱. این بوی بد کد در کدام یک از پنج دسته قرار می‌گیرد؟

این بوی بد در دسته **جفت‌کننده‌ها (Couplers)** قرار می‌گیرد. این دسته از بوها به وابستگی بیش از حد بین کلاس‌ها اشاره دارند. در `Feature Envy`، یک متد به داده‌های یک کلاس دیگر بیشتر از داده‌های کلاس خودش وابسته است که این خود نوعی از وابستگی شدید را نشان می‌دهد.

#### ۲. چه ریفکتورینگ‌هایی برای رفع این بو پیشنهاد می‌شود؟

برای از بین بردن این بو، اصل اساسی این است که کد و داده‌هایی که با آن کار می‌کنند، در کنار یکدیگر قرار گیرند. ریفکتورینگ‌های پیشنهادی عبارتند از:

- **Move Method (انتقال متد):** اگر یک متد به وضوح باید به کلاس دیگری منتقل شود، از این روش استفاده می‌شود تا متد به کلاسی که بیشترین داده را از آن فراخوانی می‌کند، منتقل گردد.
- **Extract Method (استخراج متد):** اگر تنها بخشی از یک متد به داده‌های کلاس دیگر حسادت می‌کند، می‌توان آن بخش را به یک متد جدید استخراج کرد و سپس آن متد جدید را به کلاس مورد نظر انتقال داد.

#### ۳. در چه زمانی باید این بو را نادیده گرفت؟

گاهی اوقات، رفتار (متدها) به صورت عمدی از داده‌ها جدا نگه داشته می‌شود. این حالت معمولاً زمانی اتفاق می‌افتد که از الگوهای طراحی خاصی مانند **Strategy** یا **Visitor** استفاده می‌شود. در این الگوها، هدف اصلی این است که بتوان رفتار را به صورت پویا و در زمان اجرا تغییر داد، بنابراین جدایی منطق از داده یک تصمیم طراحی آگاهانه است و نباید به عنوان یک بوی بد در نظر گرفته شود.

---

### سوال هشتم | در وبسایت ۲۹ بوی بد کد نامبرده شده است. سعی کنید ۱۰ بوی بد را در پروژه تبدیل کننده مدل به سی پیدا کنید و به آن اشاره کنید.

#### ۱. کلاس بزرگ (Large Class)

- **دسته:** متورم‌ها (Bloaters)
- **توضیح:** کلاس `ClassStructure` مسئولیت‌های بسیار زیادی را بر عهده گرفته است. این کلاس علاوه بر نگهداری داده‌های مربوط به سازنده‌ها، خصوصیات و متدها، منطق مدیریت وابستگی‌ها و تولید XML را نیز در خود جای داده است و با بیش از ۳۰۰ خط کد، درک و نگهداری آن دشوار است.

#### ۲. لیست پارامتر طویل (Long Parameter List)

- **دسته:** متورم‌ها (Bloaters)
- **توضیح:** تعریف کلاس `ClassStructure` از انواع ژنریک (Generic Types) بسیار طولانی و پیچیده‌ای استفاده می‌کند که خوانایی و استفاده از این کلاس را به شدت کاهش می‌دهد.
  ```java
  public class ClassStructure<TType extends ValueType, TAttribute extends ClassAttribute<TType>
          , TConstructor extends ClassConstructor<TType, TAttribute>,
          TMethod extends ClassMethod<TType, TAttribute>>
  ```

#### ۳. کد تکراری (Duplicate Code)

- **دسته:** موارد قابل حذف (Dispensables)
- **توضیح:** در متد `getElementDocument`، الگوی ایجاد یک عنصر XML و افزودن متن به آن بارها تکرار شده است. این تکرار باعث افزایش حجم کد شده و نگهداری آن را دشوار می‌کند، زیرا برای یک تغییر کوچک باید چندین بخش ویرایش شود.

  ```java
  Element name = document.createElement("name");
  name.appendChild(document.createTextNode(getName()));
  root.appendChild(name);

  Element superClass = document.createElement("super");
  superClass.appendChild(document.createTextNode(getSuperClass()));
  root.appendChild(superClass);
  ```

#### ۴. کلاس خدا (God Class)

- **دسته:** متورم‌ها (Bloaters)
- **توضیح:** کلاس `Phase1CodeGenerator` یک نمونه بارز از "کلاس خدا" است که مسئولیت‌های متعددی مانند تولید فایل‌های C، هدر و C++، مدیریت ورودی/خروجی فایل‌ها و پردازش دیاگرام را به تنهایی بر عهده دارد. این تمرکز بیش از حد مسئولیت‌ها، اصل مسئولیت واحد را نقض می‌کند.

#### ۵. کلاس داده (Data Class)

- **دسته:** موارد قابل حذف (Dispensables)
- **توضیح:** کلاس `ClassStructure` بیشتر به عنوان یک نگهدارنده داده با تعداد زیادی متدهای getter و setter ساده عمل می‌کند و فاقد رفتار (behavior) معنادار است. منطقی که باید درون این کلاس باشد، به کلاس‌های دیگر منتقل شده است.
  ```java
  public Vector<TConstructor> getConstructors() { ... }
  public Vector<TAttribute> getAttributes() { ... }
  public void setName(String name) { ... }
  ```

#### ۶. کامنت‌ها (Comments)

- **دسته:** موارد قابل حذف (Dispensables)
- **توضیح:** وجود کامنت‌های `TODO` نشان‌دهنده کارهای ناتمام یا تصمیمات طراحی است که به آینده موکول شده‌اند. این کامنت‌ها بدهی فنی (Technical Debt) را نشان می‌دهند و باید در اسرع وقت برطرف شوند.
  ```java
  ///TODO delete this and move all functions into Complete* classes
  public class Phase1CodeGenerator { ... }
  ```

#### ۷. متد طولانی (Long Method)

- **دسته:** متورم‌ها (Bloaters)
- **توضیح:** متد سازنده (constructor) کلاس `Phase1CodeGenerator` بسیار طولانی است و وظایف متعددی از جمله ایجاد فایل‌های مختلف، نوشتن در جریان‌های خروجی (OutputStreams) و پردازش کلاس‌ها را انجام می‌دهد. این حجم از عملیات باید به متدهای کوچک‌تر و تخصصی‌تر شکسته شود.

#### ۸. حسادت به ویژگی (Feature Envy)

- **دسته:** جفت‌کننده‌ها (Couplers)
- **توضیح:** در سازنده کلاس `CompleteClass`، این متد علاقه و وابستگی شدیدی به داده‌های کلاس `ClassStructure` نشان می‌دهد و به طور مکرر از متدهای getter آن برای مقداردهی فیلدهای خود استفاده می‌کند. این منطق احتمالاً باید به کلاس `ClassStructure` منتقل شود.
  ```java
  setName(structure.getName());
  setSuperClass(structure.getSuperClass());
  setHavingDestructor(structure.isHavingDestructor());
  ```

#### ۹. وسواس به انواع ابتدایی (Primitive Obsession)

- **دسته:** متورم‌ها (Bloaters)
- **توضیح:** در پروژه به جای ایجاد کلاس‌های کوچک و معنادار برای مفاهیم دامنه، از انواع داده اولیه مانند `String` و `Vector` به صورت گسترده استفاده شده است. این کار خوانایی و ایمنی نوع (Type Safety) کد را کاهش می‌دهد.
  ```java
  private String superClass = "null";
  private String name;
  private final Vector<TConstructor> constructors = new Vector<>();
  ```

#### ۱۰. کد مرده (Dead Code)

- **دسته:** موارد قابل حذف (Dispensables)
- **توضیح:** وجود کدهای کامنت‌شده (مانند `/*...*/`) یا متدهایی که احتمالاً هرگز فراخوانی نمی‌شوند (مانند `unsetSuperClass`)، کدبیس را شلوغ کرده و درک آن را برای توسعه‌دهندگان دشوار می‌کند. این بخش‌های غیرضروری باید حذف شوند.

  ```java
  public void unsetSuperClass() {
      /*...*/
  }
  ```

  ***

### سوال نهم | در انتها بگویید پلاگین `formatter` چه می کند و چرا می‌تواند کمک کننده باشد و رابطه آن با بازآرایی کد چیست؟

یک پلاگین **formatter** یا **Code Formatter** ابزاری است که به صورت خودکار، کد منبع (source code) را بر اساس مجموعه‌ای از قوانین و استایل‌های از پیش تعریف‌شده، مرتب و قالب‌بندی می‌کند. این ابزارها به جنبه‌های ظاهری کد مانند تورفتگی‌ها (indentation)، فاصله بین عملگرها، شکستن خطوط طولانی، و ترتیب قرارگیری پرانتزها و آکولادها می‌پردازند، بدون اینکه منطق یا عملکرد کد را تغییر دهند.

استفاده از `formatter` مزایای زیادی دارد، از جمله:

- **خوانایی و یکپارچگی کد (Readability and Consistency):** با اعمال یک استایل یکسان در تمام پروژه، کد خواناتر شده و درک آن برای تمام اعضای تیم آسان‌تر می‌شود. این یکپارچگی به خصوص در پروژه‌های تیمی اهمیت بالایی دارد.
- **کاهش بحث‌های بیهوده:** `formatter` ها بحث‌های سلیقه‌ای و غیرضروری در مورد استایل کدنویسی (مانند محل قرارگیری آکولادها) را از بین می‌برند، زیرا همه ملزم به پیروی از یک استاندارد واحد و خودکار هستند.
- **تمرکز بر روی منطق برنامه:** وقتی توسعه‌دهنده نگران قالب‌بندی کد نباشد، می‌تواند تمام تمرکز خود را بر روی حل مسئله و پیاده‌سازی منطق اصلی برنامه بگذارد.
- **شناسایی آسان‌تر خطاها:** کدی که به صورت تمیز و یکپارچه قالب‌بندی شده باشد، به شناسایی بصری خطاها، مانند بلوک‌های ناقص یا پرانتزهای جا افتاده، کمک می‌کند.

`formatter` ها و **ریفکتورینگ** هر دو با هدف بهبود کیفیت کد انجام می‌شوند، اما در دو سطح کاملاً متفاوت عمل می‌کنند:

- **تمرکز Formatter:** این ابزارها تنها بر روی **ظاهر و ساختار ظاهری** کد تمرکز دارند. آن‌ها کد را "زیباتر" و خواناتر می‌کنند، اما ساختار منطقی، طراحی، یا بهینگی آن را تغییر نمی‌دهند. قالب‌بندی کد اولین و سطحی‌ترین گام برای تمیز کردن آن است.
- **تمرکز Refactoring:** ریفکتورینگ یک فرآیند عمیق‌تر است که بر روی **بهبود ساختار داخلی و طراحی** کد تمرکز دارد، بدون اینکه رفتار خارجی آن تغییر کند. هدف ریفکتورینگ، کاهش پیچیدگی، حذف کدهای تکراری، و بهبود طراحی برای ساده‌سازی نگهداری و توسعه آینده است (مانند رفع بوهای بد کد).

**ارتباط اصلی این دو مفهوم** در این است که قالب‌بندی خودکار کد (Formatting) اغلب به عنوان **پیش‌نیاز یا اولین قدم در فرآیند ریفکتورینگ** در نظر گرفته می‌شود. وقتی کد از نظر ظاهری تمیز و یکپارچه باشد، درک ساختار آن برای توسعه‌دهنده آسان‌تر می‌شود و او می‌تواند با دید بهتری مشکلات عمیق‌تر طراحی را شناسایی کرده و فرآیند ریفکتورینگ را آغاز کند. به عبارت دیگر، `formatter` کد را برای ریفکتورینگ آماده می‌کند.

---

### منابع

برخی منابع استفاده شده به صورت زیر است:

- [Refactoring Guru](https://refactoring.guru/)
- [DigitalOcean - SOLID Principles](https://www.digitalocean.com/community/tutorials/s-o-l-i-d-the-first-five-principles-of-object-oriented-design)
- [Strategy vs State Pattern](https://medium.com/@iamprovidence/strategy-vs-state-pattern-6168cc102c91)
- [The Ultimate Guide to Code Formatters Plugin in VS Code](https://aidilfitra.medium.com/the-ultimate-guide-to-code-formatters-plugin-in-vs-code-every-language-one-editor-3457d248b5d1)

در پاسخ به سوالات از هوش مصنوعی‌های مختلف کمک گرفته شده است.
