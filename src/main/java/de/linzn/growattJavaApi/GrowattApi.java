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

package de.linzn.growattJavaApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.linzn.growattJavaApi.devices.MinDeviceData;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class GrowattApi {

    private static final String API_URL ="https://openapi.growatt.com";

    private final String token;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GrowattApi(String token) {
        this.token = token;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public MinDeviceData getMinDeviceData(String serialNumber) throws Exception {
        String formData = "pageNum=1" + "&tlxs=" + URLEncoder.encode(serialNumber, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/v1/device/tlx/tlxs_data"))
                .header("token", token)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new RuntimeException("Growatt API Error: HTTP " + response.statusCode());
        }

        JsonNode root = objectMapper.readTree(response.body());

        if (root.path("error_code").asInt() != 0) {
            throw new RuntimeException("API Error: " + root.path("error_msg").asText());
        }

        JsonNode dataNode = root
                .path("data")
                .path(serialNumber)
                .path(serialNumber);

        if (dataNode.isMissingNode()) {
            throw new RuntimeException("No data for serialnumber: " + serialNumber);
        }

        return objectMapper.treeToValue(dataNode,MinDeviceData.class);
    }
}