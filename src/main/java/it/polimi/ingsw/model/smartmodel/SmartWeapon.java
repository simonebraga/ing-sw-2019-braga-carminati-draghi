package it.polimi.ingsw.model.smartmodel;

import it.polimi.ingsw.model.enumeratedclasses.WeaponName;

public class SmartWeapon {
    private WeaponName weaponName;
    private Boolean isLoaded;

    public WeaponName getWeaponName() {
        return weaponName;
    }

    public Boolean getLoaded() {
        return isLoaded;
    }

    public void setWeaponName(WeaponName weaponName) {
        this.weaponName = weaponName;
    }

    public void setLoaded(Boolean loaded) {
        isLoaded = loaded;
    }

    @Override
    public boolean equals(Object obj) {
        SmartWeapon smartWeapon = (SmartWeapon) obj;
        return ((smartWeapon.getWeaponName() == this.weaponName) && (smartWeapon.getLoaded() == this.isLoaded));
    }
}