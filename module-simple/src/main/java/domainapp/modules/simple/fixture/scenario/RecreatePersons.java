/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package domainapp.modules.simple.fixture.scenario;

import com.google.common.collect.Lists;
import domainapp.modules.simple.dom.impl.Person;
import domainapp.modules.simple.fixture.data.PersonMenu_create;
import domainapp.modules.simple.fixture.teardown.SimpleModuleTearDown;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Accessors(chain = true)
public class RecreatePersons extends FixtureScript {

    public final List<String> NAMES = Collections.unmodifiableList(Arrays.asList(
            "Zuckerberg", "Musk", "Elison", "Frodo", "Froyo", "Fizz", "Bip", "Bop", "Bang", "Boo"));

    public final List<String> FIRST_NAMES = Collections.unmodifiableList(Arrays.asList(
            "Mark", "Elon", "Larry", "Frodo", "Froyo", "Fizz", "Bip", "Bop", "Bang", "Boo"));

    /**
     * The number of objects to create, up to 10; optional, defaults to 3.
     */
    @Getter @Setter
    private Integer number;

    /**
     * The persons created by this fixture (output).
     */
    @Getter
    private final List<Person> persons = Lists.newArrayList();

    @Override
    protected void execute(final ExecutionContext ec) {

        // defaults
        final int number = defaultParam("number", ec, 3);

        // validate
        if(number < 0 || number > NAMES.size()) {
            throw new IllegalArgumentException(String.format("number must be in range [0,%d)", NAMES.size()));
        }

        // execute
        ec.executeChild(this, new SimpleModuleTearDown());
        for (int i = 0; i < number; i++) {
            final String firstName = FIRST_NAMES.get(i);
            final String name = NAMES.get(i);
            final PersonMenu_create fs = new PersonMenu_create().setFirstName(firstName).setName(name);
            ec.executeChild(this, fs.getName(), fs);
            persons.add(fs.getPerson());
        }
    }

}
