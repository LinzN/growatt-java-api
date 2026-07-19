# Growatt-Java-Api

A lightweight Java library for connecting to the official [Growatt OpenAPI](https://openapi.growatt.com) to fetch live data from Growatt inverters and battery storage systems.

## Supported device types

Growatt's OpenAPI groups devices into several types:

| Device type | Description | Status |
|---|---|---|
| `inv` | Inverter | ❌ Not implemented |
| `storage` | Storage | ❌ Not implemented |
| `max` | MAX | ❌ Not implemented |
| `sph` | SPH | ❌ Not implemented |
| `spa` | SPA | ❌ Not implemented |
| `min` | MIN | ✅ Implemented |
| `wit` | WIT | ❌ Not implemented |
| `sph-s` | SPH-S | ❌ Not implemented |
| `noah` | NOAH | ❌ Not implemented |

Only the `MIN` device type is currently implemented. Contributions to add support for the other device types are welcome.

## Features

- Simple HTTP client based on `java.net.http.HttpClient` (no extra HTTP dependencies)
- Automatic JSON mapping of API responses via Jackson
- Typed data model (`MinDeviceData`) with PV, grid, and battery values
- Convenience methods to determine battery state (charging / discharging / idle)

## Requirements

- Java 25 or higher
- Maven
- A valid API token for the Growatt OpenAPI

## Installation

The project is not currently published to Maven Central. Clone the repository and install it into your local Maven repository:

```bash
git clone https://github.com/LinzN/growatt-java-api.git
cd growatt-java-api
mvn install
```

Then add it as a dependency in your project:

```xml
<dependency>
    <groupId>de.linzn</groupId>
    <artifactId>growatt-java-api</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Usage

```java
import de.linzn.growattJavaApi.GrowattApi;
import de.linzn.growattJavaApi.devices.MinDeviceData;

public class Example {
    public static void main(String[] args) throws Exception {
        GrowattApi api = new GrowattApi("YOUR_API_TOKEN");

        MinDeviceData data = api.getMinDeviceData("SERIAL_NUMBER");

        System.out.println("PV power: " + data.getPpv() + " W");
        System.out.println("AC power: " + data.getPac() + " W");
        System.out.println("Battery SOC: " + data.getBmsSoc() + " %");
        System.out.println("Battery state: " + data.getBatteryState());
    }
}
```

## API reference

### `GrowattApi`

| Method | Description |
|---|---|
| `GrowattApi(String token)` | Creates a new API instance with the given Growatt token |
| `MinDeviceData getMinDeviceData(String serialNumber)` | Fetches the current live data of a `MIN`-type device by its serial number |

### `MinDeviceData`

Contains, among others, the following values:

- **PV**: `ppv`, `ppv1`, `ppv2`
- **Grid/AC**: `pac`, `pacToLocalLoad`, `pacToGridTotal`, `pacToUserTotal`, `powerOfGridTake`
- **Battery**: `bmsSoc`, `bmsSoh`, `bmsVbat`, `bmsIbat`, `bmsTemp1Bat`, `bdc1ChargePower`, `bdc1DischargePower`, `bdc1Vbat`, `bdc1Ibat`, `chargePowerOfBattery`, `disChargePowerOfBattery`
- **Energy counters**: `echargeToday`, `echargeTotal`, `edischargeToday`, `edischargeTotal`
- **Status**: `status`, `faultType`, `warnCode`, `time`, `serialNum`

The class also provides convenience methods:

```java
data.isCharging();      // true if the battery is currently charging
data.isDischarging();   // true if the battery is currently discharging
data.isIdle();          // true if the battery is idle
data.getBatteryState(); // returns CHARGING, DISCHARGING, or IDLE
```

### `BatteryState`

Enum with the values `CHARGING`, `DISCHARGING`, `IDLE`.

## Error handling

On an HTTP error or an error code other than `0` in the API response, `getMinDeviceData` throws a `RuntimeException` with the corresponding error message. If no data record is found for the given serial number, a `RuntimeException` is also thrown.

## License

This project is licensed under the [GNU Lesser General Public License v3.0 (LGPLv3)](https://www.gnu.org/licenses/lgpl-3.0.html).

## Author

Niklas Linz – MirraNET
Contact: niklas.linz@mirranet.de

## Disclaimer

This is an unofficial project and is not affiliated with Growatt. Use at your own risk.
