# Cal's Chili: Open Interfaces Demo (Act I, Java)

Companion project for the ACC FedLab talk "The Secret of Cal's Chili."
Plain Java 21 + Maven, one package (`kitchen`), no frameworks.

## Open in IntelliJ IDEA

1. File > Open, pick this folder (the one with `pom.xml`), choose "Open as Project."
2. Let Maven import finish (bottom status bar). It downloads JUnit 5 the first time.
3. If prompted for a JDK, pick any JDK 21 (File > Project Structure > SDK > Download JDK works).
4. View > Appearance > Enter Presentation Mode before you go on screen.

## What's in the kitchen

| File | Slide | What it shows |
| --- | --- | --- |
| `ChiliMaker.java` | 7, 8 | The interface. One method, one promise. |
| `Cal.java` | 8 | Encapsulation: private recipe and pantry, public `makeChili`. |
| `SecretRecipe.java`, `Pantry.java`, `Ingredient.java`, `Chili.java` | 7 | Supporting objects. |
| `PeekAttempt.java` | 9 | Uncomment one line to get "recipe has private access in Cal." |
| `LeakyCal.java` | 10 | The convenience getter that gives away the recipe. |
| `JohnsInstantPot.java`, `CanOfChili.java` | 7, 11 | Other implementations of the same interface. |
| `Potluck.java` | 11 | Polymorphism: same call, three kitchens. |
| `ChiliMakerConformanceTest.java` | 12, 19 | The health inspector. The can fails on purpose. |

## Demo sequence

The presenter script (Google Doc) has every step with expected output.
Only the trucks and the menu board are containers; the browser is the customer.
One command per line, from this folder:

```
docker build -t cals-chili:1.0 .
docker run -d --name cals-truck -p 8080:8080 -e KITCHEN=cal cals-chili:1.0
docker run -d --name menu-board -p 8081:8080 -e URL=http://localhost:8080/openapi.yaml swaggerapi/swagger-ui:v5.17.14
docker run -d --name vendor-b-truck -p 8082:8080 -e KITCHEN=instantpot -e "TRUCK_NAME=Vendor B Chili Co." cals-chili:1.0
docker run -d --name can-truck -p 8083:8080 -e KITCHEN=can -e "TRUCK_NAME=Can Truck" cals-chili:1.0
docker logs cals-truck
docker rm -f cals-truck vendor-b-truck can-truck menu-board
```

Order from http://localhost:8081: open POST /orders, click Try it out, pick the truck in the
Servers box just above Execute (not the one at the top of the page), and click Execute.

`customer/HungryCustomer.java` and `customer/Inspector.java` are optional extras that do the
same ordering and checking from code. They are not used in the live demo.

## Plan B (no containers)

If Rancher misbehaves on the day, run the same thing straight from IntelliJ:
run `truck.ChiliTruck` (it listens on 8080), then run `customer.HungryCustomer` and `customer.Inspector`.
To run a second truck, copy the run configuration and set environment variables `KITCHEN=instantpot;PORT=8082`.
