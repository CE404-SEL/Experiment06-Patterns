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
