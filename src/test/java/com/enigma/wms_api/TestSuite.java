package com.enigma.wms_api;

import com.enigma.wms_api.controller.BranchControllerTest;
import com.enigma.wms_api.controller.ProductControllerTest;
import com.enigma.wms_api.controller.TransactionControllerTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({BranchControllerTest.class, ProductControllerTest.class, TransactionControllerTest.class})
public class TestSuite {

}