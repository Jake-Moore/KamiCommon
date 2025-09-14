package com.kamikazejam.kamicommon.util.teleport;

import com.kamikazejam.kamicommon.nms.text.VersionedComponent;
import com.kamikazejam.kamicommon.util.teleport.ps.PS;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

@Setter
@SuppressWarnings("unused")
public class DestinationSimple extends DestinationAbstract {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    protected @Nullable PS ps;

    // -------------------------------------------- //
    // CONSTRUCT
    // -------------------------------------------- //

    public DestinationSimple() {
        this(null, null);
    }

    public DestinationSimple(@Nullable PS ps) {
        this(ps, null);
    }

    public DestinationSimple(@Nullable PS ps, @Nullable VersionedComponent desc) {
        this.ps = ps;
        this.desc = desc;
    }

    // -------------------------------------------- //
    // OVERRIDE
    // -------------------------------------------- //

    @Override
    public @Nullable PS getPsInner() {
        return this.ps;
    }

}
