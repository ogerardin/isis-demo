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
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.applib.services.title.TitleService;

import javax.inject.Inject;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.VersionStrategy;
import java.util.Date;

@javax.jdo.annotations.PersistenceCapable(
        identityType=IdentityType.DATASTORE,
        schema = "simple",
        table = "Mission"
)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=javax.jdo.annotations.IdGeneratorStrategy.IDENTITY,
         column="id")
@javax.jdo.annotations.Version(
        strategy= VersionStrategy.DATE_TIME,
        column="version")
@DomainObject(
        objectType = "simple.Mission",
        auditing = Auditing.ENABLED,
        publishing = Publishing.ENABLED
)
public class Mission implements Comparable<Mission> {


    //region > constructor
    public Mission(final Person person, String customer, Date startDate) {
        this.person = person;
        this.startDate = startDate;
        this.customer = customer;
    }
    //endregion

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.DISABLED
    )
    @Getter @Setter
    private Person person;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.ENABLED
    )
    @Title(sequence = "1")
    @Getter @Setter
    private String customer;

    @javax.jdo.annotations.Column(allowsNull = "false")
    @Property(
            editing = Editing.ENABLED
    )
    @Title(sequence = "2")
    @Getter @Setter
    private Date startDate;

    @javax.jdo.annotations.Column(allowsNull = "true")
    @Property(
            editing = Editing.ENABLED
    )
    @Title(sequence = "3", prepend = "-")
    @Getter @Setter
    private Date endDate;

    public String validateEndDate(Date endDate) {
        if (! endDate.after(getStartDate())) {
            return "End date must be after start date";
        }
        return null;
    }

    @Override
    public int compareTo(Mission mission) {
        return getStartDate().compareTo(mission.getStartDate());
    }

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
                extends SimpleModuleDomSubmodule.PropertyDomainEvent<Mission, String> { }
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

    //region > delete (action)
    @Mixin(method = "exec")
    public static class delete {

        public static class ActionDomainEvent extends SimpleModuleDomSubmodule.ActionDomainEvent<Mission> {
        }

        private final Mission mission;
        @Inject
        public delete(final Mission mission) {
            this.mission = mission;
        }

        @Action(
                domainEvent = ActionDomainEvent.class,
                semantics = SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE
        )
        @ActionLayout(
                contributed = Contributed.AS_ACTION
        )
        public void exec() {
            final String title = titleService.titleOf(mission);
            messageService.informUser(String.format("'%s' deleted", title));
            repositoryService.remove(mission);
        }

        @Inject
        RepositoryService repositoryService;

        @Inject
        TitleService titleService;

        @Inject
        MessageService messageService;
    }

    //endregion

}