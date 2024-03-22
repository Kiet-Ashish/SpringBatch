package com.example.springbatchrest.Service;

import com.example.springbatchrest.Entity.Todo;
import com.example.springbatchrest.Repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RestJobService {

    private final TodoRepository repository;
    @Autowired
    public RestJobService(TodoRepository repository){
        this.repository = repository;
    }
    private List<Todo> listOne = null;
    public List<Todo> restToDB(){
        RestTemplate template = new RestTemplate();
        String url = "https://jsonplaceholder.typicode.com/todos";
        Todo[] arr = template.getForObject(url, Todo[].class);
        List<Todo> list = new ArrayList<>();
        assert arr != null;
        Collections.addAll(list, arr);
        return list;
    }

    public List<Todo> getAllTodo(){
        return repository.findAll();
    }

    // we need to give data one by one.
    public Todo oneAtTime(){

        if(listOne == null){
            listOne = restToDB();
        }

        if(!listOne.isEmpty()){
            return listOne.remove(0);
        }
        return null;
    }


}
