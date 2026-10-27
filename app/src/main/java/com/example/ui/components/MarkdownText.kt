package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanPrimary

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val blocks = parseMarkdownBlocks(text)

    SelectionContainer {
        Column(modifier = modifier) {
            blocks.forEach { block ->
                when (block) {
                    is MarkdownBlock.Heading -> {
                        Text(
                            text = block.text,
                            style = when (block.level) {
                                1 -> MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary,
                                    fontSize = 20.sp
                                )
                                2 -> MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyanPrimary,
                                    fontSize = 18.sp
                                )
                                else -> MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = CyanPrimary,
                                    fontSize = 16.sp
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    is MarkdownBlock.CodeBlock -> {
                        CodeBlockView(
                            code = block.code,
                            language = block.language
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    is MarkdownBlock.BulletItem -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "• ",
                                color = CyanPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = formatInlineMarkdown(block.text, color),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                    is MarkdownBlock.NumberedItem -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "${block.number}. ",
                                color = CyanPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = formatInlineMarkdown(block.text, color),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp
                                )
                            )
                        }
                    }
                    is MarkdownBlock.Paragraph -> {
                        Text(
                            text = formatInlineMarkdown(block.text, color),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeBlockView(
    code: String,
    language: String
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        color = Color(0xFF0F172A),
        tonalElevation = 2.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = language.ifBlank { "code" }.uppercase(),
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Code",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(12.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

private sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock
    data class CodeBlock(val code: String, val language: String) : MarkdownBlock
    data class BulletItem(val text: String) : MarkdownBlock
    data class NumberedItem(val number: String, val text: String) : MarkdownBlock
    data class Paragraph(val text: String) : MarkdownBlock
}

private fun parseMarkdownBlocks(rawText: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = rawText.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // Code block starts
        if (line.trim().startsWith("```")) {
            val language = line.trim().removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            if (i < lines.size) i++ // skip closing fence
            blocks.add(MarkdownBlock.CodeBlock(codeLines.joinToString("\n"), language))
            continue
        }

        // Heading
        if (line.startsWith("#")) {
            val level = line.takeWhile { it == '#' }.length
            val headingText = line.drop(level).trim()
            if (headingText.isNotEmpty()) {
                blocks.add(MarkdownBlock.Heading(level, headingText))
            }
            i++
            continue
        }

        // Bullet point
        val trimmed = line.trim()
        if (trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("+ ")) {
            val content = trimmed.drop(2).trim()
            blocks.add(MarkdownBlock.BulletItem(content))
            i++
            continue
        }

        // Numbered list
        val numberedRegex = Regex("""^(\d+)\.\s+(.*)""")
        val match = numberedRegex.find(trimmed)
        if (match != null) {
            val num = match.groupValues[1]
            val content = match.groupValues[2]
            blocks.add(MarkdownBlock.NumberedItem(num, content))
            i++
            continue
        }

        // Normal paragraph (combine non-empty lines)
        if (trimmed.isNotEmpty()) {
            blocks.add(MarkdownBlock.Paragraph(line))
        }

        i++
    }

    return blocks
}

private fun formatInlineMarkdown(text: String, baseColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        // Match bold **text** or inline code `code` or italic *text*
        val inlineRegex = Regex("""(\*\*([^*]+)\*\*)|(`([^`]+)`)|(\*([^*]+)\*)""")
        val matches = inlineRegex.findAll(text)

        for (m in matches) {
            val range = m.range
            if (range.first > currentIndex) {
                append(text.substring(currentIndex, range.first))
            }

            when {
                m.groupValues[1].isNotEmpty() -> { // Bold
                    val inner = m.groupValues[2]
                    val start = length
                    append(inner)
                    addStyle(
                        SpanStyle(fontWeight = FontWeight.Bold, color = baseColor),
                        start,
                        length
                    )
                }
                m.groupValues[3].isNotEmpty() -> { // Inline Code
                    val inner = m.groupValues[4]
                    val start = length
                    append(inner)
                    addStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0x3338BDF8),
                            color = CyanPrimary,
                            fontWeight = FontWeight.Medium
                        ),
                        start,
                        length
                    )
                }
                m.groupValues[5].isNotEmpty() -> { // Italic
                    val inner = m.groupValues[6]
                    val start = length
                    append(inner)
                    addStyle(
                        SpanStyle(fontStyle = FontStyle.Italic, color = baseColor),
                        start,
                        length
                    )
                }
            }
            currentIndex = range.last + 1
        }

        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }
}
