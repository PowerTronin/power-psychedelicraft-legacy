# Fluids Containers and Pipes

## Units

Мод использует Fabric fluid units.

Основные объемы:

- Bucket: 81000.
- Glass Bottle: 27000.
- Bowl: 4050.
- Mug: 40500.
- Cup: 20250.
- Chalice: 16200.
- Shot: 3240.
- Bottle: 162000.
- Syringe: 810.
- Barrel: 648000.
- Flask: 648000.
- Mash Tub: 729000.

## Containers

Bottle:

- Большой переносимый container.
- Нужен для Bunsen Burner large mode.
- Подходит для bottle rack.

Filled Glass Bottle:

- Vanilla-style small container.
- Используется в drug receptacle recipes.

Syringe:

- Маленький injectable container.
- Подходит для injectable drug fluids.

Flask:

- Block/container на 8 buckets.
- Удобен как buffer после Distillery или Bunsen Burner.

## Glass Tube

Glass Tube переносит fluid packets.

Поведение:

- У каждой трубы есть input и output direction.
- ПКМ stick по трубе меняет input/output местами.
- Трубы продвигают packet вперед через scheduled ticks.
- Если output не принимает жидкость, packet может вернуться или быть wasted.

Temperature:

- Жидкость в pipe имеет temperature от 0 до 15.
- При переносе температура падает.
- Ice/snow рядом усиливают охлаждение.
- Fire/campfire/lava рядом уменьшают охлаждение или нагревают.
- Tray принимает condensate, когда fluid temperature ниже condensation point.

## Glass Valve

Glass Valve наследует pipe behavior, но может блокировать поток.

Использование:

- ПКМ открывает/закрывает valve.
- Удобно ставить перед Tray.
- Позволяет не залить неправильную жидкость в setup.

## Pump

Pump выкачивает still source-block fluids из мира по redstone.

Поведение:

- При redstone signal появляется Pump Head.
- Pump ищет source fluid впереди.
- Output идет в трубу позади.
- Pump пытается передать 1 bucket как 10 packets.

Практика:

- Поставь Pump лицом к fluid source.
- Сзади поставь Glass Tube.
- Подай redstone pulse.

## Tray Input

Tray принимает input только сверху.

Правильная схема:

```text
Glass Tube output down
        |
      Tray
```

Если Tray уже hardening или результат готов, он отвергает новые fluids.

