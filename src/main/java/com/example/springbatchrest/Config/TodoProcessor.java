package com.example.springbatchrest.Config;

import com.example.springbatchrest.Entity.Todo;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TodoProcessor implements ItemProcessor<Todo, Todo> {
    @Override
    public Todo process(Todo item) throws Exception {
        item.setTitle("Ashish sharma");
        return item;
    }
}
