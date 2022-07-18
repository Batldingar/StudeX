package at.hagenberg.studex.core

data class QuestionsOrderedByPDF(
    var id: Int,
    var question: String,
    var answer: String,
    var difficulty: Int,
    var pdfId: Int?,
    var document_name: String,
)