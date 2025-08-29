# MiniJava
Mini-Java is a subset of Java. MiniJava compiler implement a compiler for the Mini-java
programming language.

# Rules of MiniJava
```
Goal --> Source EOF
Source --> ClassDeclarations MainClass
MainClass --> class Identifier { public static void main() { VarDeclarations Statements}}
ClassDeclarations --> ClassDeclaration ClassDeclarations | lambda
ClassDeclaration --> class Identifier Extension { FieldDeclarations MethodDeclarations }
Extension --> extends Identifier | lambda
FieldDeclarations --> FieldDeclaration FieldDeclarations | lambda
FieldDeclaration --> static Type Identifier ;
VarDeclarations --> VarDeclaration VarDeclarations | lambda
VarDeclaration --> Type Identifier ;
MethodDeclarations --> MethodDeclaration MethodDeclarations | lambda
MethodDeclaration --> public static Type Identifier ( Parameters ) { VarDeclarations Statements return GenExpression ; }
Parameters --> Type Identifier Parameter | lambda
Parameter --> , Type Identifier Parameter | lambda
Type --> boolean | int
Statements --> Statements Statement | lambda
Statement --> { Statements } | if ( GenExpression ) Statement else Statement | while ( GenExpression ) Statement | System.out.println ( GenExpression ) ; | Identifier = GenExpression ;
GenExpression --> Expression | RelExpression
Expression --> Expression + Term | Expression - Term | Term
Term --> Term * Factor | Factor
Factor --> ( Expression ) | Identifier | Identifier . Identifier | Identifier . Identifier ( Arguments ) | true | false | Integer
RelExpression --> RelExpression && RelTerm | RelTerm
RelTerm --> Expression == Expression | Expression < Expression
Arguments --> GenExpression Argument | lambda
Argument --> , GenExpression Argument | lambda
Identifier --> <IDENTIFIER_LITERAL>
Integer --> <INTEGER_LITERAL>
```



# بازآرایی‌ها  

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
