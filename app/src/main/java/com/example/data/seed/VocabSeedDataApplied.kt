package com.example.data.seed

import com.example.data.model.VocabCard

object VocabSeedDataApplied {
    fun getAllSeedCards(): List<VocabCard> {
        val all = ArrayList<VocabCard>(4600)
        // Part 1 to 9 (984 cards)
        all.addAll(VocabSeedDataPart1.getCards())
        all.addAll(VocabSeedDataPart2.getCards())
        all.addAll(VocabSeedDataPart3.getCards())
        all.addAll(VocabSeedDataPart4.getCards())
        all.addAll(VocabSeedDataPart5.getCards())
        all.addAll(VocabSeedDataPart6.getCards())
        all.addAll(VocabSeedDataPart7.getCards())
        all.addAll(VocabSeedDataPart8.getCards())
        all.addAll(VocabSeedDataPart9.getCards())

        // Shinkanzen Master 1 to 8 (565 cards)
        all.addAll(VocabSeedDataShinkanzen1.getCards())
        all.addAll(VocabSeedDataShinkanzen2.getCards())
        all.addAll(VocabSeedDataShinkanzen3.getCards())
        all.addAll(VocabSeedDataShinkanzen4.getCards())
        all.addAll(VocabSeedDataShinkanzen5.getCards())
        all.addAll(VocabSeedDataShinkanzen6.getCards())
        all.addAll(VocabSeedDataShinkanzen7.getCards())
        all.addAll(VocabSeedDataShinkanzen8.getCards())

        // Hnin 1 to 10 (768 cards)
        all.addAll(VocabSeedDataHnin1.getCards())
        all.addAll(VocabSeedDataHnin2.getCards())
        all.addAll(VocabSeedDataHnin3.getCards())
        all.addAll(VocabSeedDataHnin4.getCards())
        all.addAll(VocabSeedDataHnin5.getCards())
        all.addAll(VocabSeedDataHnin6.getCards())
        all.addAll(VocabSeedDataHnin7.getCards())
        all.addAll(VocabSeedDataHnin8.getCards())
        all.addAll(VocabSeedDataHnin9.getCards())
        all.addAll(VocabSeedDataHnin10.getCards())

        // Essential 1 to 8 (577 cards)
        all.addAll(VocabSeedDataEssential1.getCards())
        all.addAll(VocabSeedDataEssential2.getCards())
        all.addAll(VocabSeedDataEssential3.getCards())
        all.addAll(VocabSeedDataEssential4.getCards())
        all.addAll(VocabSeedDataEssential5.getCards())
        all.addAll(VocabSeedDataEssential6.getCards())
        all.addAll(VocabSeedDataEssential7.getCards())
        all.addAll(VocabSeedDataEssential8.getCards())

        // Mastery 1 to 4 (333 cards)
        all.addAll(VocabSeedDataMastery1.getCards())
        all.addAll(VocabSeedDataMastery2.getCards())
        all.addAll(VocabSeedDataMastery3.getCards())
        all.addAll(VocabSeedDataMastery4.getCards())

        // Past Exams 1 to 4 (1357 cards)
        all.addAll(VocabSeedDataPastExams1.getCards())
        all.addAll(VocabSeedDataPastExams2.getCards())
        all.addAll(VocabSeedDataPastExams3.getCards())
        all.addAll(VocabSeedDataPastExams4.getCards())

        return all
    }
}
