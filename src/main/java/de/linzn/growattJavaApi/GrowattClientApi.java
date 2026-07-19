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

import de.linzn.growattJavaApi.devices.min.MinDeviceApiWrapper;
import de.linzn.growattJavaApi.devices.min.MinDeviceRealTimeData;
import de.linzn.growattJavaApi.exceptions.GrowattApiException;
import de.linzn.growattJavaApi.exceptions.GrowattDeviceNotFoundException;

public class GrowattClientApi {

    private final String token;

    public GrowattClientApi(String token) {
        this.token = token;
    }

    /**
     * Request realtime data from MIN device
     * @param serialNumber SerialNumber of MIN device
     * @return RealTime data as MinDeviceRealTimeData
     * @throws GrowattApiException
     * @throws GrowattDeviceNotFoundException
     */
    public MinDeviceRealTimeData getMinDeviceRealTimeData(String serialNumber) throws GrowattApiException, GrowattDeviceNotFoundException {
        return MinDeviceApiWrapper.getMinDeviceRealTimeData(token, serialNumber);
    }
}