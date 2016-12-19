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

package domainapp.modules.simple.fixture.data;

import domainapp.modules.simple.dom.impl.Person;
import domainapp.modules.simple.dom.impl.PersonMenu;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.isis.applib.fixturescripts.FixtureScript;

@Accessors(chain = true)
public class PersonMenu_create extends FixtureScript {

    /**
     * Name of the object (required)
     */
    @Getter @Setter
    private String name;

    @Getter @Setter
    private String firstName;

    /**
     * The created simple object (output).
     */
    @Getter
    private Person person;


    @Override
    protected void execute(final ExecutionContext ec) {

        String firstName = checkParam("firstName", ec, String.class);
        String name = checkParam("name", ec, String.class);

        this.person = wrap(personMenu).create(firstName, name);
        ec.addResult(this, person);
    }

    @javax.inject.Inject
    PersonMenu personMenu;

}
