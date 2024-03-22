package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Organization;
import org.springframework.batch.item.ItemProcessor;

public class OrganizationProcessor implements ItemProcessor<Organization, Organization> {
    @Override
    public Organization process(Organization item) throws Exception {
        item.setId(null);
        return item;
    }
}
