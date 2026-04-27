# Effects Config and Troubleshooting

## Drug Effects

Мод имеет собственную систему drug properties.

Основные drug types:

- Alcohol.
- Cannabis.
- Tobacco.
- Cocaine.
- Caffeine.
- Sugar.
- Bath Salts.
- LSD.
- Atropine.
- Morphine.
- Methamphetamine.
- Peyote.
- Magic mushrooms.
- Kava.
- Sleep deprivation.

Возможные эффекты:

- Visual shaders.
- Color shifts.
- Waves/pulses/fractals.
- Bloom/color bloom.
- Hallucinated entities.
- Movement/attribute changes.
- Hunger/metabolism changes.
- Audio/chat distortions.
- Sleep restrictions.

## Shader and Visuals

Если эффекты выглядят странно:

- Проверь Iris/Sodium compatibility.
- Проверь, что resource assets загружены без missing model/blockstate/sound ошибок.
- Убедись, что запускаешь jar, собранный после generated assets fix.

## Config Options

Точные имена config settings лучше сверять в generated config после первого запуска, но важные группы такие:

- Worldgen toggles и spawn chances.
- Balancing для drying/mashing/maturation.
- Enabling/disabling Rift Jars.
- Enabling/disabling Harmonium.
- Molotov-related toggle.
- Villager trade toggles.

## Known Practical Issues

Missing generated assets/data:

- Симптомы: missing blockstates/models/sounds, crash при входе в мир, неработающие recipes/tags.
- Решение в форке: `src/main/generated` теперь закоммичен и попадает в jar.

Bunsen Burner output wasted:

- Симптомы: реакция идет, но жидкость исчезает.
- Причина: сверху нет Flask/Glass Tube/Tray input.
- Решение: подключить output перед запуском.

Tray не наполняется:

- Симптомы: ПКМ container по Tray ничего не делает.
- Причина: Tray принимает только pipe input сверху.
- Решение: использовать Glass Tube output down.

Blue Crystal Meth не получается:

- Симптомы: получается обычный Crystal Meth.
- Причина: lapis_lazuli impurity не дошла до Tray.
- Решение: вести output напрямую из Bunsen Burner через Glass Tube в Tray, не через Flask.

Drying Table не сушит:

- Симптомы: progress стоит.
- Причины: дождь, низкое тепло, нет света.
- Решение: поставить под крышу, добавить light/heat, использовать Iron Drying Table.

## Useful Commands For Development

Build:

```bash
./gradlew clean build
```

Generate data:

```bash
./gradlew runDatagen
```

Run client:

```bash
./gradlew runClient
```

