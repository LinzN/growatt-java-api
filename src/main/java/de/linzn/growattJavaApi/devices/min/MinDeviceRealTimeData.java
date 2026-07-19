/*
 * Copyright (c) 2026 MirraNET, Niklas Linz. All rights reserved.
 *
 * This file is part of the MirraNET project and is licensed under the
 * GNU Lesser General Public License v3.0 (LGPLv3).
 *
 * You may use, distribute and modify this code under the terms
 * of the LGPLv3 license. You should have received a copy of the
 * license along with this file. If not, see <https://www.gnu.org/licenses/lgpl-3.0.html>
 * or contact: niklas.linz@mirranet.de
 */

package de.linzn.growattJavaApi.devices.min;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.linzn.growattJavaApi.BatteryState;
import de.linzn.growattJavaApi.exceptions.GrowattApiException;
import de.linzn.growattJavaApi.exceptions.GrowattDeviceNotFoundException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinDeviceRealTimeData {

    private int bmsSoc;

    private double ppv;

    private double ppv1;

    private double ppv2;

    private double pac;

    private double pacToLocalLoad;

    private double pacToGridTotal;

    private double pacToUserTotal;

    private double bdc1ChargePower;

    private double bdc1DischargePower;

    private double chargePowerOfBattery;

    private double disChargePowerOfBattery;

    private double bmsVbat;

    private double bdc1Vbat;

    private double bmsIbat;

    private double bdc1Ibat;

    private double bmsTemp1Bat;

    private int bmsSoh;

    private double echargeToday;

    private double echargeTotal;

    private double edischargeToday;

    private double edischargeTotal;

    private int status;

    private int faultType;

    private int warnCode;

    private String time;

    private String serialNum;

    private double powerOfGridTake;

    public int getBmsSoc() {
        return bmsSoc;
    }

    public double getPpv() {
        return ppv;
    }

    public double getPpv1() {
        return ppv1;
    }

    public double getPpv2() {
        return ppv2;
    }

    public double getPac() {
        return pac;
    }

    public double getPacToLocalLoad() {
        return pacToLocalLoad;
    }

    public double getPacToGridTotal() {
        return pacToGridTotal;
    }

    public double getPacToUserTotal() {
        return pacToUserTotal;
    }

    public double getBdc1ChargePower() {
        return bdc1ChargePower;
    }

    public double getBdc1DischargePower() {
        return bdc1DischargePower;
    }

    public double getChargePowerOfBattery() {
        return chargePowerOfBattery;
    }

    public double getDisChargePowerOfBattery() {
        return disChargePowerOfBattery;
    }

    public double getBmsVbat() {
        return bmsVbat;
    }

    public double getBdc1Vbat() {
        return bdc1Vbat;
    }

    public double getBmsIbat() {
        return bmsIbat;
    }

    public double getBdc1Ibat() {
        return bdc1Ibat;
    }

    public double getBmsTemp1Bat() {
        return bmsTemp1Bat;
    }

    public int getBmsSoh() {
        return bmsSoh;
    }

    public double getEchargeToday() {
        return echargeToday;
    }

    public double getEchargeTotal() {
        return echargeTotal;
    }

    public double getEdischargeToday() {
        return edischargeToday;
    }

    public double getEdischargeTotal() {
        return edischargeTotal;
    }

    public double getPowerOfGridTake() {
        return powerOfGridTake;
    }

    public int getStatus() {
        return status;
    }

    public int getFaultType() {
        return faultType;
    }

    public int getWarnCode() {
        return warnCode;
    }

    public String getTime() {
        return time;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public boolean isCharging() {
        return bdc1ChargePower > 0
                && bdc1DischargePower == 0;
    }

    public boolean isDischarging() {
        return bdc1DischargePower > 0
                && bdc1ChargePower == 0;
    }

    public boolean isIdle() {
        return bdc1ChargePower == 0
                && bdc1DischargePower == 0;
    }

    public BatteryState getBatteryState() {
        if (isCharging()) {
            return BatteryState.CHARGING;
        }

        if (isDischarging()) {
            return BatteryState.DISCHARGING;
        }

        return BatteryState.IDLE;
    }

    static MinDeviceRealTimeData call(String API_URL, String token, String serialNumber) throws GrowattApiException, GrowattDeviceNotFoundException {
        HttpClient httpClient = HttpClient.newHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        String formData = "pageNum=1" + "&tlxs=" + URLEncoder.encode(serialNumber, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/v1/device/tlx/tlxs_data"))
                .header("token", token)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (IOException | InterruptedException e) {
            throw new GrowattApiException(e);
        }

        if (response.statusCode() != 200) {
            throw new GrowattApiException("Growatt API Error: HTTP " + response.statusCode());
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(response.body());
        } catch (JsonProcessingException e) {
            throw new GrowattApiException(e);
        }

        if (root.path("error_code").asInt() != 0) {
            throw new GrowattApiException("API Error: " + root.path("error_msg").asText());
        }

        JsonNode dataNode = root
                .path("data")
                .path(serialNumber)
                .path(serialNumber);

        if (dataNode.isMissingNode()) {
            throw new GrowattDeviceNotFoundException("Device not found with this serial number: " + serialNumber);
        }

        try {
            return objectMapper.treeToValue(dataNode, MinDeviceRealTimeData.class);
        } catch (JsonProcessingException e) {
            throw new GrowattApiException(e);
        }
    }
}