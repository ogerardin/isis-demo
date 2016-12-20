package domainapp.modules.simple.dom.impl;

import org.apache.isis.applib.annotation.*;

@DomainService(nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY)
public class PersonContributions {

    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @Action(semantics = SemanticsOf.SAFE)
    @Property(editing = Editing.DISABLED)
    public int missionCount(Person person) {
        return person.getMissions().size();
    }

    @Action(semantics = SemanticsOf.IDEMPOTENT_ARE_YOU_SURE)
    public Person capitalize(Person person) {
        person.setFirstName(
                person.getFirstName().substring(0,1).toUpperCase()
                +person.getFirstName().substring(1).toLowerCase()
        );
        person.setName(
                person.getName().substring(0,1).toUpperCase()
                +person.getName().substring(1).toLowerCase()
        );
        return person;
    }


}
