# MediaTek Bypass Charging

A kernel and system-level implementation that adds bypass charging support to MediaTek Android devices.

The project integrates bypass charging into the Android Settings application and provides a simple interface for controlling charging behavior.

## Features

* Adds a **Bypass Charging** option to the **Battery** section of Android Settings.
* Provides kernel-level bypass charging control through:

  ```text
  /proc/mtk_battery_cmd/current_cmd
  ```
* Uses a background **system service** to enable and disable bypass charging.
* Provides an **Automatic Charging Threshold** setting that automatically starts charging when the battery level falls below the configured threshold.
* Integrates bypass charging directly into the Android system.

## Compatibility

Designed for MediaTek devices that provide the following battery control interface:

```text
/proc/mtk_battery_cmd/current_cmd
```

Device and kernel support for this interface is required.

## Tested Device

| Device                     | SoC                | Android    | ROM            |
| -------------------------- | ------------------ | ---------- | -------------- |
| Infinix Hot 40 Pro (X6837) | MediaTek Helio G99 | Android 16 | LineageOS 23.2 |

## Device Tree Integration

Add the following to the device tree:

```makefile
# MTK Bypass Charge

$(call inherit-product, vendor/mtk-bypass-charge/mtk.mk)
```

The bypass charging logic and the `/proc/mtk_battery_cmd/current_cmd` interface are based on the work from [AZenith](https://github.com/Liliya2727/AZenith).

## License

GNU General Public License v2.0
