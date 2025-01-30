/****************************\
 *      ________________      *
 *     /  _             \     *
 *     \   \ |\   _  \  /     *
 *      \  / | \ / \  \/      *
 *      /  \ | / | /  /\      *
 *     /  _/ |/  \__ /  \     *
 *     \________________/     *
 *                            *
 \****************************/
/*
 * Copyright 2025 Damien Westerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.damienwesterman.defensedrill.gateway.service;

import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * Dev service class to interact with the HashiCorp Vault KMS.
 */
@Profile({"dev", "default"})
@Service
@RequiredArgsConstructor
public class DevVaultService implements VaultService {
    @Override
    @NonNull
    public String getJwtPublicKey() {
        // Return development public key
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqp7lE/z+pO6APn6boPj1b6w9n/p7PiJ5PPul0+VF7QHFzOO6JGfBY81e/sMKoEsoZEqf6ew1ZH77PJ9AxDZ6mkgFzPnaOsbbYi6NXiDBu9C0+P1RSMKEX96cz+F4YDN9SgkcMcBndsNGUusWvzt87o0jR93ynr70OG+JAE5131gxYfV9DDDmTaAx/KarqCcgLCf98KpIGfMUqs6X/BXo3MMAIanXMbmvfeBeLZeEHGrlr2w80fw3DgRqKV8dCHRUUDuB7Vr1Fz/sV8cq26XG6vsSzZi1YKzjd3Kd3pBL0xEtimlk5rLRlxqazodXzNbv2AY2z95HbxaOupwW479zSwIDAQAB";
    }
}
