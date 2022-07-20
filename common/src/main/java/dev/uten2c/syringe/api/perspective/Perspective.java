package dev.uten2c.syringe.api.perspective;

public enum Perspective {
    FIRST_PERSON(true, false),
    THIRD_PERSON_BACK(false, false),
    THIRD_PERSON_FRONT(false, true),
    ;

    private final boolean firstPerson;
    private final boolean frontView;

    Perspective(boolean firstPerson, boolean frontView) {
        this.firstPerson = firstPerson;
        this.frontView = frontView;
    }

    public boolean isFirstPerson() {
        return firstPerson;
    }

    public boolean isFrontView() {
        return frontView;
    }
}
