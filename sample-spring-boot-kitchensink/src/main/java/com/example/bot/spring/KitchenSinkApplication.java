/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * The class KitchenSinkApplication is the main class. It runs the reply system and the push message system.
 *
 */
@SpringBootApplication
public class KitchenSinkApplication {
    static Path downloadedContentDir;
    
    /**
     * This method is the main() of the system. It runs the reply system. In addtion, it sets the observer pattern and starts the timer. 
     * @param args This is used in the main() function
     * @throws IOException This throws an exception when errors happen in the input stream and output stream.
     */
    public static void main(String[] args) throws IOException {
        downloadedContentDir = Files.createTempDirectory("line-bot");
        TimeManager tm = TimeManager.getTimer();
        Discount discount = new Discount();
        PaymentReminder pr = new PaymentReminder();
        NotifyingCustomer notifying = new NotifyingCustomer();
        tm.addObserver(discount);
        tm.addObserver(pr);
        tm.addObserver(notifying);
        tm.timing();
        SpringApplication.run(KitchenSinkApplication.class, args);
        
    }

}
