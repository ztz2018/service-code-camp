package me.pointofreview.api;

import lombok.extern.slf4j.Slf4j;
import me.pointofreview.core.data.filter.CodeSnippetsFilter;
import me.pointofreview.core.objects.*;
import me.pointofreview.persistence.ModelDataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class OrchestratorService {

    private final ModelDataStore dataStore;

    @Autowired
    public OrchestratorService(@Qualifier("mongoModelDataStore") ModelDataStore dataStore) {
        this.dataStore = dataStore;
    }

    @GetMapping("/snippets/recent")
    public List<CodeSnippet> recentSnippets(@RequestParam(value="maximumNumber", defaultValue="10") int maximumNumber) {
        return dataStore.getCodeSnippets(new CodeSnippetsFilter(new ArrayList<>(), CodeSnippetsFilter.SortBy.RECENT, maximumNumber));
    }

    @GetMapping("/snippets/popular")
    public ResponseEntity<List<CodeSnippet>> popularSnippets(@RequestParam(value="maximumNumber", defaultValue="10") int maximumNumber) {
        var result = dataStore.getCodeSnippets(new CodeSnippetsFilter(new ArrayList<>(), CodeSnippetsFilter.SortBy.POPULARITY, maximumNumber));
        if(result == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/snippets/users/{userId}")
    public ResponseEntity<List<CodeSnippet>> getSnippetsByUserId(@PathVariable(name = "userId") String userId) {
        var result = dataStore.getCodeSnippetsByUserId(userId);
        if(result == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/snippets/tag")
    public ResponseEntity<List<CodeSnippet>> getSnippetsByTag(@RequestBody String tagName) {
        var result = dataStore.getCodeSnippetByTag(tagName);
        if (result == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/snippets/tags")
    public ResponseEntity<List<CodeSnippet>> getSnippetsByTags(@RequestBody List<String> tagNames) {
        var result = dataStore.getCodeSnippetByTags(tagNames);
        if (result == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/snippets/{id}")
    public ResponseEntity<CodeSnippet> getSnippet(@PathVariable(name = "id") String id) {
        var result = dataStore.getCodeSnippet(id);

        if(result == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/snippets")
    public ResponseEntity<CodeSnippet> createCodeSnippet(@RequestBody CodeSnippet snippet) {
        snippet.setId(UUID.randomUUID().toString());
        var result = dataStore.createCodeSnippet(snippet);
        if(!result)
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        return new ResponseEntity<>(snippet, HttpStatus.OK);
    }

    @PostMapping("/reviews")
    public ResponseEntity<CodeReview> createCodeReview(@RequestBody CodeReview review) {
        review.setId(UUID.randomUUID().toString());
        var result = dataStore.addCodeReview(review);
        if(!result)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    @PostMapping("/reviews/comment")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        comment.setId(UUID.randomUUID().toString());
        var result = dataStore.createComment(comment);
        if(!result)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @PostMapping("/reviews/sections/impressions")
    public ResponseEntity<Score> updateSectionImpressions(@RequestBody ImpressionRequest request) {
        var score = dataStore.updateCodeReviewSectionImpressions(request.snippetId,request.codeReviewId,request.codeReviewSectionId,request.voterId,request.impression);
        return new ResponseEntity<>(score, HttpStatus.OK);
    }

    @PostMapping("/snippets/impressions")
    public ResponseEntity<Score> updateSnippetImpressions(@RequestBody ImpressionRequest request) {
        var snippet = dataStore.getCodeSnippet(request.snippetId);
        if (snippet == null){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        dataStore.updateCodeSnippetImpressions(snippet,request.voterId,request.impression);
        return new ResponseEntity<>(snippet.getScore(), HttpStatus.OK);

    }

}
