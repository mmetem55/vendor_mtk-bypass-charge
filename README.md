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
* Provides an Automatic Charging Threshold setting, automatically starts charging when the battery level falls below the configured threshold.
* Integrates bypass charging directly into the Android system.

## Compatibility

Designed for MediaTek devices that provide the following battery control interface:

```text
/proc/mtk_battery_cmd/current_cmd
```

Device and kernel support for this interface is required.

## License

GPL-2.0-only
