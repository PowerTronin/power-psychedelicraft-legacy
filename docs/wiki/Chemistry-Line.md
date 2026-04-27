# Chemistry Line

Этот раздел описывает только внутриигровую механику мода. Он не является реальной химической инструкцией.

## Основные блоки

- Bunsen Burner: reactions, extraction, purification.
- Glass Tube: перенос горячих fluid packets.
- Glass Valve: труба с ручным open/close.
- Pump: redstone-насос для source fluids из мира.
- Flask: большой fluid buffer.
- Tray: hardening/crystallisation endpoint.

## Как работает Bunsen Burner

Практичная сборка:

```text
Glass Tube / Flask / Tray input
        ^
        |
 Bunsen Burner
        |
 Redstone or heat source
```

Шаги:

1. Поставь Bunsen Burner.
2. Поставь на него модовый Bottle.
3. Залей воду или другую нужную жидкость.
4. Добавь твердые ingredients.
5. Подай redstone signal или heat source снизу.
6. Подключи output сверху.

Важно:

- Для recipes с твердыми ingredients используй именно модовый Bottle.
- Если output сверху не подключен, жидкий продукт будет wasted.
- При перегреве пустой burner может ломать стекло и наносить damage рядом.

## Reacting Recipes

Основные реакции:

- Water + Belladonna Seeds -> Belladonna Extract + Coal.
- Water + Jimsonweed Seeds -> Jimsonweed Extract + Coal.
- Water + Morning Glory или Morning Glory Seeds -> Morning Glory Extract + Coal.
- Water + Poppy -> Morphine + Coal.
- Water + Coal -> Petrolium с petrolium impurity.
- Water + Charcoal -> Petrolium с carbon impurity.
- Water + Coal Block -> больше Petrolium с petrolium impurity.

Additions/impurities:

- Broken Glass -> silica.
- Sugar -> sugar.
- Lapis Lazuli -> lapis_lazuli.
- Lapis Block -> больше lapis_lazuli.

Impurities важны для некоторых Tray recipes. Если жидкость идет через Flask, impurity-метка может не сохраниться. Для таких recipes веди output из burner в Tray напрямую через Glass Tube.

## Extract Purification

Chemical extracts можно дальше греть в Bunsen Burner без новых твердых ingredients.

Morning Glory line:

- Morning Glory Extract.
- Morning Glory Perfume.
- Morning Glory Concentrate.
- Acid.

Belladonna line:

- Belladonna Extract.
- Belladonna Perfume.
- Belladonna Concentrate.
- Atropine.

Jimsonweed line:

- Jimsonweed Extract.
- Jimsonweed Perfume.
- Jimsonweed Concentrate.
- Atropine.

## Tray Recipes

Tray принимает жидкость сверху через Glass Tube и начинает hardening после заполнения.

Рецепты:

- Acid -> LSD Tablet.
- Morning Glory Extract без lapis_lazuli impurity -> Crystal Meth.
- Morning Glory Extract с lapis_lazuli impurity -> Blue Crystal Meth.
- Morphine -> Heroine.
- Ethanol + Cocaine solution -> Crack Cocaine.
- Water -> Sugar.

Результат обычно 3-6 items. Некоторые impurities увеличивают output count, но также меняют effects.

## Practical Lines

LSA Square:

- Сделай Morning Glory Extract.
- Purify до Morning Glory Concentrate.
- Craft Paper + Morning Glory Concentrate -> LSA Square.

LSD Tablet:

- Сделай Morning Glory Extract.
- Purify до Acid.
- Подай Acid в Tray.

Blue Crystal Meth:

- Сделай Morning Glory Extract.
- Добавь lapis_lazuli addition в Bunsen Burner.
- Выведи жидкость напрямую через трубы в Tray.

Morphine Tablet:

- Water + Poppy -> Morphine.
- Morphine -> Tray -> Heroine.
- 4 Heroine в 2x2 -> Morphine Tablet.

Crack Cocaine:

- Получи Ethanol.
- Получи Cocaine fluid.
- Подай Ethanol как base в Tray.
- Подай Cocaine solution в тот же Tray.

## Petrolium and Gasoline

Petrolium:

- Water + Coal/Charcoal/Coal Block в Bunsen Burner.

Gasoline:

- Petrolium -> Distillery -> Gasoline.

Обе жидкости горючие. Храни их отдельно от огня, lava и случайного redstone-triggered setup.

