# Stations and Recipes

## Drying Table

Drying Table сушит растительное сырье и некоторые vanilla-предметы.

Рабочие рецепты сушки:

- Brown Mushroom -> Brown Magic Mushrooms.
- Red Mushroom -> Red Magic Mushrooms.
- Coca Leaves -> Dried Coca Leaves.
- Belladonna Leaf -> Dried Belladonna Leaf.
- Tobacco Leaves -> Dried Tobacco.
- Cannabis Buds -> Dried Cannabis Buds.
- Cannabis Leaf -> Dried Cannabis Leaf.
- Jimsonweed Leaf -> Dried Jimsonweed Leaf.
- Peyote -> Dried Peyote.
- Poppy -> Dried Poppy.

Поведение:

- Slot 0 является output.
- Slots 1-9 являются input.
- Скорость зависит от света и температуры биома.
- Дождь блокирует сушку, если стол видит небо.
- Зажженная furnace/smoker/blast furnace рядом может ускорить нагрев.
- Iron Drying Table использует более быстрый таймер.

## Mash Tub

Mash Tub используется для mashing и fermentation.

Основные свойства:

- Вместимость: 9 buckets.
- Принимает fluid containers и ингредиенты по ПКМ.
- Может подбирать item entities рядом, если они подходят под текущий recipe.
- Если жидкости нет, оставшиеся ингредиенты можно вернуть.

Mashing обычно требует воду и набор ингредиентов. Примеры:

- Wheat + Hop Cones -> Wheat Hop base.
- Red Grapes -> Red Grapes base.
- Apple -> Apple base.
- Honey -> Honey base.
- Potato/Corn/Wheat -> соответствующие alcohol bases.
- Juniper Berries + Grapes + Sugar + Wheat -> Juniper base.
- Agave Leaf -> Agave base.

## Barrel

Barrel нужен для maturation.

Свойства:

- Вместимость: 8 buckets.
- Работает с alcohol fluids после fermentation.
- Есть варианты под разные wood types.

Шаблон barrel recipe:

```text
 I 
# #
S#S

I = iron ingot
# = matching planks
S = sticks
```

## Flask

Flask является большим fluid buffer.

Свойства:

- Вместимость: 8 buckets.
- Может принимать и отдавать fluids через интерфейс и pipes.
- Удобен после Distillery и Bunsen Burner.

Ограничение: при переносе через Flask impurity-метки из pipe-потока могут быть потеряны. Для рецептов Tray, которым важны impurities, лучше вести жидкость напрямую через Glass Tube.

## Distillery

Distillery перегоняет подходящие processable fluids.

Источник тепла снизу:

- Lava: сильный нагрев.
- Fire или lit campfire: средний нагрев.
- Magma block: слабый нагрев.

Distillery должен иметь выход в Flask или совместимый блок. Для alcohol fluids distillation работает только после fermentation и до maturation.

## Bunsen Burner

Bunsen Burner используется для reaction и purification.

Практичная схема:

- Поставь Bunsen Burner.
- Поставь на него Bottle.
- Залей жидкость, обычно water.
- Добавь ингредиенты.
- Подай redstone signal или поставь heat source снизу.
- Снимай продукт трубой сверху.

Ограничения:

- Glass Bottle/Potion/Filled Glass Bottle дают small mode.
- Модовый Bottle дает large mode и позволяет добавлять твердые ингредиенты.
- Если output не подключен, жидкий продукт будет теряться.
- Если burner пустой и перегрет, он может сломать стекло и повредить entities рядом.

## Tray

Tray кристаллизует/затвердевает жидкость.

Поведение:

- Вместимость: 50 internal fluid units.
- Принимает fluid только сверху через pipe.
- Нельзя наполнить обычным ПКМ.
- После hardening результат хранится в block entity.
- Забрать результат проще всего, сломав Tray.

Основные рецепты:

- Acid -> LSD Tablet.
- Morning Glory Extract без lapis_lazuli impurity -> Crystal Meth.
- Morning Glory Extract с lapis_lazuli impurity -> Blue Crystal Meth.
- Morphine -> Heroine.
- Ethanol + Cocaine solution -> Crack Cocaine.
- Water -> Sugar.

