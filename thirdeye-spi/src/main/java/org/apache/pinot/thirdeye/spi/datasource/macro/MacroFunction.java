package org.apache.pinot.thirdeye.spi.datasource.macro;

import java.util.List;

public interface MacroFunction {

  String name();

  String expandMacro(List<String> macroParams, MacroFunctionContext context);
}