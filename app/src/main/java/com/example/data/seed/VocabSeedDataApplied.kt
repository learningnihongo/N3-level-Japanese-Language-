package com.example.data.seed

import com.example.data.model.VocabCard

object VocabSeedDataApplied {
    fun getAllSeedCards(): List<VocabCard> {
        val all = mutableListOf<VocabCard>()
        all.addAll(VocabSeedDataPart1.getCards())
        all.addAll(VocabSeedDataPart3.getCards())
        all.addAll(VocabSeedDataPart4.getCards())
        all.addAll(VocabSeedDataPart6.getCards())
        all.addAll(VocabSeedDataPart7.getCards())
        all.addAll(VocabSeedDataPart9.getCards())
        all.addAll(VocabSeedDataShinkanzen2.getCards())
        all.addAll(VocabSeedDataShinkanzen5.getCards())
        all.addAll(VocabSeedDataShinkanzen6.getCards())
        all.addAll(VocabSeedDataShinkanzen7.getCards())
        all.addAll(VocabSeedDataShinkanzen8.getCards())
        all.addAll(VocabSeedDataHnin1.getCards())
        all.addAll(VocabSeedDataHnin2.getCards())
        all.addAll(VocabSeedDataHnin4.getCards())
        all.addAll(VocabSeedDataHnin5.getCards())
        all.addAll(VocabSeedDataHnin7.getCards())
        all.addAll(VocabSeedDataEssential1.getCards())
        all.addAll(VocabSeedDataEssential3.getCards())
        all.addAll(VocabSeedDataEssential4.getCards())
        all.addAll(VocabSeedDataEssential6.getCards())
        all.addAll(VocabSeedDataEssential7.getCards())
        all.addAll(VocabSeedDataMastery2.getCards())
        all.addAll(VocabSeedDataMastery4.getCards())
        all.addAll(VocabSeedDataPastExams2.getCards())
        all.addAll(VocabSeedDataPastExams3.getCards())
        return all
    }
}
