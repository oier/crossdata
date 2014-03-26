/*
 * Stratio Meta
 *
 * Copyright (c) 2014, Stratio, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.stratio.meta.core.validator.statements;

import com.stratio.meta.common.result.MetaResult;
import com.stratio.meta.core.cassandra.BasicCoreCassandraTest;
import com.stratio.meta.core.metadata.MetadataManager;
import com.stratio.meta.core.statements.CreateKeyspaceStatement;
import com.stratio.meta.core.structures.IdentifierProperty;
import com.stratio.meta.core.structures.ValueProperty;
import com.stratio.meta.core.validator.BasicValidatorTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class CreateKeyspaceStatementTest extends BasicValidatorTest {

    @Test
    public void validate_ok(){
        //TODO Migrate the tests to validateOk or validateFail. Take into account the mapping order.
        String name = "not_exists";
        boolean ifNotExists = false;
        Map<String, ValueProperty> properties = new HashMap<>();
        IdentifierProperty ip = new IdentifierProperty("{class: SimpleStrategy, replication_factor: 1}");
        properties.put("replication", ip);

        CreateKeyspaceStatement cks = new CreateKeyspaceStatement(name, ifNotExists, properties);
        System.out.println("{"+cks.toString()+"}");
        MetaResult result = cks.validate(_metadataManager, "");
        assertNotNull(result, "Sentence validation not supported");
        assertFalse(result.hasError(), "Cannot validate sentence");
    }

    @Test
    public void validate_ifNotExists_ok(){
        String name = "demo";
        boolean ifNotExists = true;
        Map<String, ValueProperty> properties = new HashMap<>();
        IdentifierProperty ip = new IdentifierProperty("{class: SimpleStrategy, replication_factor: 1}");
        properties.put("replication", ip);

        CreateKeyspaceStatement cks = new CreateKeyspaceStatement(name, ifNotExists, properties);
        MetaResult result = cks.validate(_metadataManager, "");
        assertNotNull(result, "Sentence validation not supported");
        assertFalse(result.hasError(), "Cannot validate sentence");
    }


    @Test
    public void validate_missingProperties(){
        String name = "not_exists";
        boolean ifNotExists = false;
        Map<String, ValueProperty> properties = new HashMap<>();

        CreateKeyspaceStatement cks = new CreateKeyspaceStatement(name, ifNotExists, properties);
        MetaResult result = cks.validate(_metadataManager, "");
        assertNotNull(result, "Sentence validation not supported");
        assertTrue(result.hasError(), "Validation should fail");
    }

    @Test
    public void validate_missingReplication(){
        String name = "demo";
        boolean ifNotExists = true;
        Map<String, ValueProperty> properties = new HashMap<>();
        IdentifierProperty ip = new IdentifierProperty("{invalid_value}");
        properties.put("missing", ip);

        CreateKeyspaceStatement cks = new CreateKeyspaceStatement(name, ifNotExists, properties);
        MetaResult result = cks.validate(_metadataManager, "");
        assertNotNull(result, "Sentence validation not supported");
        assertTrue(result.hasError(), "Validation should fail");
    }
}
