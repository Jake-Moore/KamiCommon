package com.kamikazejam.kamicommon.util.mson;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class MsonEventType {
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	public static MsonEventType CLICK = new MsonEventType() {
		@Contract(pure = true)
		@Override
		public MsonEvent get(@NotNull Mson mson) {
			return mson.clickEvent;
		}

		@Contract("_, _ -> new")
		@Override
		public @NotNull Mson set(@NotNull Mson mson, MsonEvent event) {
			return Mson.valueOf(
					mson.getText(),
					mson.getColor(),
					mson.isBold(),
					mson.isItalic(),
					mson.isUnderlined(),
					mson.isStrikethrough(),
					mson.isObfuscated(),
					event,
					mson.getEvent(HOVER),
					mson.getInsertion(),
					mson.getExtra(),
					mson.getParent()
			);
		}
	};

	public static MsonEventType HOVER = new MsonEventType() {
		@Contract(pure = true)
		@Override
		public MsonEvent get(@NotNull Mson mson) {
			return mson.hoverEvent;
		}

		@Contract("_, _ -> new")
		@Override
		public @NotNull Mson set(@NotNull Mson mson, MsonEvent event) {
			return Mson.valueOf(
					mson.getText(),
					mson.getColor(),
					mson.isBold(),
					mson.isItalic(),
					mson.isUnderlined(),
					mson.isStrikethrough(),
					mson.isObfuscated(),
					mson.getEvent(CLICK),
					event,
					mson.getInsertion(),
					mson.getExtra(),
					mson.getParent()
			);
		}
	};

	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //

	public abstract MsonEvent get(Mson mson);

	public abstract Mson set(Mson mson, MsonEvent event);

}
