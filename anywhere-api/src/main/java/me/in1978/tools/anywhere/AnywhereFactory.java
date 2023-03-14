package me.in1978.tools.anywhere;

import me.in1978.tools.anywhere.model.EngineModel;

public interface AnywhereFactory {

    AnywhereEngine createEngine(EngineModel model);

    String id();
}
