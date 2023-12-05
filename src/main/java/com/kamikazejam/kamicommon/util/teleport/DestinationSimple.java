package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.util.teleport.ps.PS;

@SuppressWarnings("unused")
public class DestinationSimple extends DestinationAbstract {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected PS ps;

    public void setPs(PS ps) {
        this.ps = ps;
    }

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationSimple() {
        this(null, null);
    }

    public DestinationSimple(PS ps) {
        this(ps, null);
    }

    public DestinationSimple(PS ps, String desc) {
        this.ps = ps;
        this.desc = desc;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public PS getPsInner() {
        return this.ps;
    }

}
