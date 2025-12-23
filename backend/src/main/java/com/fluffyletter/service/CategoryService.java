package com.fluffyletter.service;

import com.fluffyletter.dto.CategoryDTO;
import com.fluffyletter.entity.Category;
import com.fluffyletter.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> list(String lang) {
        String l = LangUtil.normalize(lang);
        return categoryRepository.findByActiveTrueOrderBySortOrderAscIdAsc()
                .stream()
                .map(c -> new CategoryDTO(c.getId(), pickName(c, l)))
                .toList();
    }

    private static String pickName(Category c, String lang) {
        return "en".equals(lang) ? c.getNameEn() : c.getNameZh();
    }
}
