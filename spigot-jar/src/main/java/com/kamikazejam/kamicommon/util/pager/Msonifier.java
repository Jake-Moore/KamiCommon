package com.kamikazejam.kamicommon.util.pager;


import com.kamikazejam.kamicommon.util.mson.Mson;

public interface Msonifier<T> {
	Mson toMson(T item, int index);
}
