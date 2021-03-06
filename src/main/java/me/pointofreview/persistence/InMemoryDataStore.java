package me.pointofreview.persistence;

import me.pointofreview.core.data.filter.CodeSnippetsFilter;
import me.pointofreview.core.objects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryDataStore implements ModelDataStore {

    private UserDataStore userDataStore;

    private Map<String, CodeSnippet> codeSnippets;

    @Autowired
    public InMemoryDataStore(@Qualifier("inMemoryUserDataStore") UserDataStore userDataStore) {
        this.userDataStore = userDataStore;
        codeSnippets = new HashMap<>();
    }

    @Override
    public List<CodeSnippet> getCodeSnippets(CodeSnippetsFilter filter) {
        switch (filter.getSortBy()) {
            case POPULARITY: return getCodeSnippetsByPopularity(filter);
            case RECENT:
            default: return getCodeSnippetsByTime(filter);
        }
    }

    private List<CodeSnippet> getCodeSnippetsByTime(CodeSnippetsFilter filter) {
        SortedMap<Long, CodeSnippet> sortedSnippets = new TreeMap<>();
        for(var snippet : codeSnippets.values()) {
            sortedSnippets.put(snippet.getTimestamp(), snippet);
        }

        List<CodeSnippet> result = new ArrayList<>();
        for(var entry : sortedSnippets.entrySet()) {
            if(filter.accept(entry.getValue()))
                result.add(entry.getValue());
            if(result.size() == filter.getMaximumNumber())
                break;
        }

        return result;
    }

    private List<CodeSnippet> getCodeSnippetsByPopularity(CodeSnippetsFilter filter) {
        SortedMap<Score, CodeSnippet> sortedSnippets = new TreeMap<>();
        for(var snippet : codeSnippets.values()) {
            sortedSnippets.put(snippet.getScore(), snippet);
        }

        List<CodeSnippet> result = new ArrayList<>();
        for(var entry : sortedSnippets.entrySet()) {
            if(filter.accept(entry.getValue()))
                result.add(entry.getValue());
            if(result.size() == filter.getMaximumNumber())
                break;
        }

        return result;
    }

    @Override
    public CodeSnippet getCodeSnippet(String snippetId) {
        return codeSnippets.get(snippetId);
    }

    @Override
    public List<CodeSnippet> getCodeSnippetsByUsername(String username) {
        var user = userDataStore.getUserById(username);
        if(user == null)
            return null;

        List<CodeSnippet> result = new ArrayList<>();
        for(var snippet : codeSnippets.values()) {
            if(snippet.getUsername().equals(username))
                result.add(snippet);
        }

        return result;
    }

    @Override
    public List<CodeSnippet> getCodeSnippetByTag(String tagName) {
        return null;
    }

    @Override
    public List<CodeSnippet> getCodeSnippetByTags(List<String> tagNames) {
        return null;
    }

    @Override
    public boolean createCodeSnippet(CodeSnippet snippet) {
        if(codeSnippets.containsKey(snippet.getId()))
            return false;

        codeSnippets.put(snippet.getId(), snippet);
        return true;
    }

    @Override
    public boolean createComment(Comment comment) {
        var snippet = codeSnippets.get(comment.getCodeSnippetId());
        if(snippet == null)
            return false;

        var review = snippet.getReview(comment.getCodeReviewId());
        if(review == null)
            return false;

        review.addComment(comment);

        return true;
    }

    @Override
    public boolean addCodeReview(CodeReview review) {
        var snippet = codeSnippets.get(review.getCodeSnippetId());
        if(snippet == null)
            return false;

        snippet.addReview(review);
        return true;
    }

    @Override
    public List<CodeSnippet> getAllCodeSnippets() {
        return null;
    }


    @Override
    public Score updateCodeReviewSectionImpressions(String snippetId, String codeReviewId, String codeReviewSectionId, String userId, Impression impression) {
        return null;
    }

    @Override
    public boolean updateCodeSnippetImpressions(CodeSnippet snippet, String userId, Impression impression) {
        if (snippet == null)
            return false;
        snippet.updateImpressions(userId,impression);
        return true;
    }

    @Override
    public void resetDatabase() {

    }
}
