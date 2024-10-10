/*
 * Copyright (c) Tuenti Technologies S.L. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tornaco.apps.shortx.services.phone.sms;

import java.util.Objects;

/**
 * Represents a sms stored in Android sms Content Provider.
 * <p/>
 * Address field is the equivalent to the MT/MO author MSISDN in telco terminology.
 * <p/>
 * Review MSISDN standard for more information: http://en.wikipedia.org/wiki/MSISDN
 *
 * @author Pedro Vcente Gómez Sánchez <pgomez@tuenti.com>
 * @author Manuel Peinado <mpeinado@tuenti.com>
 */
public class Sms {

    private final long id;
    private final String address;
    private final String date;
    private final String msg;
    private final SmsType type;

    public Sms(long id, String address, String date, String msg, SmsType type) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.msg = msg;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }

    public SmsType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sms sms = (Sms) o;
        return Objects.equals(id, sms.id) && Objects.equals(address, sms.address)
                && Objects.equals(date, sms.date) && Objects.equals(msg, sms.msg) && type == sms.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, date, msg, type);
    }

    @Override
    public String toString() {
        return "Sms{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", msg='" + msg + '\'' +
                ", type=" + type +
                '}';
    }
}
