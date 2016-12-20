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
package domainapp.modules.simple.dom.impl;

import domainapp.modules.simple.dom.SimpleModuleDomSubmodule;
import lombok.Getter;
import lombok.Setter;
import org.apache.isis.applib.annotation.*;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;
import org.apache.isis.applib.util.ObjectContracts;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import java.util.Date;
import java.util.SortedSet;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "Person"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByName", language = "JDOQL",
                value = "SELECT "
                        + "FROM domainapp.modules.simple.dom.impl.Person "
                        + "WHERE name.indexOf(:name) >= 0 ")
})
@javax.jdo.annotations.Unique(name="Person_name_UNQ", members = {"name"})
@DomainObject(
        objectType = "simple.Person",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED,
        bounded = true
)
public class Person implements Comparable<Person> {

    //region > title
    public TranslatableString title() {
        return TranslatableString.tr("Person: {name}", "name", getName());
    }
    //endregion

    //region > constructor
    public Person(final String firstName, String name) {
        setFirstName(firstName);
        setName(name);
    }
    //endregion

    //region > name (read-only property)
    public static class NameType {
        private NameType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 40;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends SimpleModuleDomSubmodule.PropertyDomainEvent<Person, String> { }
    }


    @javax.jdo.annotations.Column(allowsNull = "false", length = NameType.Meta.MAX_LEN)
    @Property(
            editing = Editing.DISABLED,
            domainEvent = NameType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String name;

    // endregion

    //region > notes (editable property)
    public static class NotesType {
        private NotesType() {
        }

        public static class Meta {
            public static final int MAX_LEN = 4000;

            private Meta() {
            }
        }

        public static class PropertyDomainEvent
                extends SimpleModuleDomSubmodule.PropertyDomainEvent<Person, String> { }
    }


    @javax.jdo.annotations.Column(
            allowsNull = "true",
            length = NotesType.Meta.MAX_LEN
    )
    @Property(
            command = CommandReification.ENABLED,
            publishing = Publishing.ENABLED,
            domainEvent = NotesType.PropertyDomainEvent.class
    )
    @Getter @Setter
    private String notes;
    //endregion

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            editing = Editing.ENABLED
    )
    @Getter @Setter
    private Date birthDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            editing = Editing.ENABLED
    )
    @Getter @Setter
    private Date hireDate;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.ENABLED
    )
    @Getter @Setter
    private String firstName;

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Person hire() {
        setHireDate(new Date());
        return this;
    }

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            editing = Editing.ENABLED
    )
    @Getter @Setter
    Person cooptedBy;

    @CollectionLayout(defaultView = "table")
    @Getter @Setter
    SortedSet<Mission> missions;


    //region > updateName (action)
    @Mixin(method = "exec")
    public static class updateName {

        public static class ActionDomainEvent extends SimpleModuleDomSubmodule.ActionDomainEvent<Person> {
        }

        private final Person person;

        public updateName(final Person person) {
            this.person = person;
        }

        @Action(
                command = CommandReification.ENABLED,
                publishing = Publishing.ENABLED,
                semantics = SemanticsOf.IDEMPOTENT,
                domainEvent = ActionDomainEvent.class
        )
        @ActionLayout(
                contributed = Contributed.AS_ACTION
        )
        public Person exec(
                @Parameter(maxLength = Person.NameType.Meta.MAX_LEN)
                @ParameterLayout(named = "Name")
                final String name) {
            person.setName(name);
            return person;
        }

        public String default0Exec() {
            return person.getName();
        }

        public TranslatableString validate0Exec(final String name) {
            return name != null && name.contains("!") ? TranslatableString.tr("Exclamation mark is not allowed") : null;
        }

    }
    //endregion

    //region > delete (action)
    @Mixin(method = "exec")
    public static class delete {

        public static class ActionDomainEvent extends SimpleModuleDomSubmodule.ActionDomainEvent<Person> {
        }

        private final Person person;
        @Inject
        public delete(final Person person) {
            this.person = person;
        }

        @Action(
                domainEvent = ActionDomainEvent.class,
                semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE
        )
        @ActionLayout(
                contributed = Contributed.AS_ACTION
        )
        public void exec() {
            final String title = titleService.titleOf(person);
            messageService.informUser(String.format("'%s' deleted", title));
            repositoryService.remove(person);
        }

        @javax.inject.Inject
        RepositoryService repositoryService;

        @javax.inject.Inject
        TitleService titleService;

        @javax.inject.Inject
        MessageService messageService;
    }

    //endregion

    public Person addMission(
            @ParameterLayout(named = "Customer") String customer,
            @ParameterLayout(named = "Start Date") Date startDate
    ) {
        Mission mission = new Mission(this, customer, startDate);
        getMissions().add(mission);
        return this;
    }

    public String default0AddMission() {
        return "BCEE";
    }

    public Date default1AddMission() {
        return new Date();
    }

    public String disableAddMission() {
        if (getHireDate() == null) {
            return "Must be hired!";
        }
        return null;
    }

    //region > toString, compareTo
    @Override
    public String toString() {
        return ObjectContracts.toString(this, "name");
    }

    @Override
    public int compareTo(final Person other) {
        return ObjectContracts.compare(this, other, "name");
    }

    //endregion

}