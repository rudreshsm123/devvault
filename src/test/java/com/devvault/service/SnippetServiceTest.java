package com.devvault.service;

import com.devvault.entity.CodeSnippet;
import com.devvault.entity.SnippetLanguage;
import com.devvault.exception.NotFoundException;
import com.devvault.repository.SnippetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnippetServiceTest {

    @Mock
    private SnippetRepository snippetRepository;

    @InjectMocks
    private SnippetService snippetService;

    private CodeSnippet sampleSnippet;

    @BeforeEach
    void setUp() {
        sampleSnippet = new CodeSnippet();
        sampleSnippet.setId(1L);
        sampleSnippet.setTitle("Hello");
        sampleSnippet.setCode("System.out.println();");
    }

    @Test
    void createSnippet_savesAndReturnsTheSnippet() {
        when(snippetRepository.save(sampleSnippet)).thenReturn(sampleSnippet);

        CodeSnippet result = snippetService.createSnippet(sampleSnippet);

        assertThat(result.getTitle()).isEqualTo("Hello");
        verify(snippetRepository).save(sampleSnippet);
    }

    @Test
    void getSnippetById_whenFound_returnsSnippet() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(sampleSnippet));

        assertThat(snippetService.getSnippetById(1L)).isEqualTo(sampleSnippet);
    }

    @Test
    void getSnippetById_whenMissing_throwsNotFoundException() {
        when(snippetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> snippetService.getSnippetById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateSnippet_overwritesFieldsAndSaves() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(sampleSnippet));
        when(snippetRepository.save(sampleSnippet)).thenReturn(sampleSnippet);

        CodeSnippet changes = new CodeSnippet();
        changes.setTitle("Hello Python");
        changes.setCode("print('hi')");
        changes.setLanguage(SnippetLanguage.PYTHON);

        CodeSnippet result = snippetService.updateSnippet(1L, changes);

        assertThat(result.getTitle()).isEqualTo("Hello Python");
        assertThat(result.getCode()).isEqualTo("print('hi')");
        assertThat(result.getLanguage()).isEqualTo(SnippetLanguage.PYTHON);
    }

    @Test
    void deleteSnippet_whenFound_deletesIt() {
        when(snippetRepository.findById(1L)).thenReturn(Optional.of(sampleSnippet));

        snippetService.deleteSnippet(1L);

        verify(snippetRepository).delete(sampleSnippet);
    }

    @Test
    void search_convertsBlankKeywordToNull() {
        when(snippetRepository.search(null, SnippetLanguage.JAVA)).thenReturn(List.of(sampleSnippet));

        List<CodeSnippet> result = snippetService.search("  ", SnippetLanguage.JAVA);

        assertThat(result).containsExactly(sampleSnippet);
    }
}
