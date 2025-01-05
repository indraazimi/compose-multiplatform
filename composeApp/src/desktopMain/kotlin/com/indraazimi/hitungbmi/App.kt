package com.indraazimi.hitungbmi

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import hitungbmi.composeapp.generated.resources.Res
import hitungbmi.composeapp.generated.resources.berat_badan
import hitungbmi.composeapp.generated.resources.bmi_intro
import hitungbmi.composeapp.generated.resources.bmi_x
import hitungbmi.composeapp.generated.resources.gemuk
import hitungbmi.composeapp.generated.resources.hitung
import hitungbmi.composeapp.generated.resources.ideal
import hitungbmi.composeapp.generated.resources.input_invalid
import hitungbmi.composeapp.generated.resources.kurus
import hitungbmi.composeapp.generated.resources.pria
import hitungbmi.composeapp.generated.resources.tinggi_badan
import hitungbmi.composeapp.generated.resources.wanita
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.pow

@Composable
@Preview
fun App() {
    MaterialTheme {
        ScreenContent()
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier) {
    var berat by remember { mutableStateOf("") }
    var beratError by remember { mutableStateOf(false) }

    var tinggi by remember { mutableStateOf("") }
    var tinggiError by remember { mutableStateOf(false) }

    val radioOptions = listOf(
        stringResource(Res.string.pria),
        stringResource(Res.string.wanita)
    )
    var gender by remember { mutableStateOf(radioOptions[0]) }

    var bmi by remember { mutableFloatStateOf(0f) }
    var kategori by remember { mutableStateOf<StringResource?>(null) }

    Column(
        modifier = modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.bmi_intro),
            style = MaterialTheme.typography.body1,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = berat,
            onValueChange = { it: String -> berat = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.berat_badan)) },
            trailingIcon = { IconPicker(beratError, "kg") },
            //supportingText = { ErrorHint(beratError) },
            isError = beratError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        OutlinedTextField(
            value = tinggi,
            onValueChange = { it: String -> tinggi = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(Res.string.tinggi_badan)) },
            trailingIcon = { IconPicker(tinggiError, "cm") },
            //supportingText = { ErrorHint(tinggiError) },
            isError = tinggiError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )
        Row(
            modifier = Modifier
                .padding(top = 6.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
        ) {
            radioOptions.forEach { text ->
                GenderOption(
                    label = text,
                    isSelected = gender == text,
                    modifier = Modifier
                        .selectable(
                            selected = gender == text,
                            onClick = { gender = text },
                            role = Role.RadioButton
                        )
                        .weight(1f)
                        .padding(16.dp)
                )
            }
        }
        Button(
            onClick = {
                beratError = (berat == "" || berat == "0")
                tinggiError = (tinggi == "" || tinggi == "0")
                if (beratError || tinggiError) return@Button

                bmi = hitungBmi(berat.toFloat(), tinggi.toFloat())
                kategori = getKategori(bmi, gender == radioOptions[0])
            },
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal=32.dp, vertical=16.dp)
        ) {
            Text(text = stringResource(Res.string.hitung))
        }

        if (bmi != 0f) {
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp
            )
            Text(
                text = stringResource(Res.string.bmi_x, bmi).dropLast(4),
                style = MaterialTheme.typography.h6
            )
            kategori?.let {
                Text(
                    text = stringResource(it).uppercase(),
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
}

@Composable
fun GenderOption(label: String, isSelected: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun IconPicker(isError: Boolean, unit: String) {
    if (isError) {
        Icon(imageVector = Icons.Filled.Warning, contentDescription = null)
    } else {
        Text(text = unit)
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError) {
        Text(text = stringResource(Res.string.input_invalid))
    }
}

private fun hitungBmi(berat: Float, tinggi: Float): Float {
    return berat / (tinggi / 100).pow(2)
}

private fun getKategori(bmi: Float, isMale: Boolean): StringResource {
    return if (isMale) {
        when {
            bmi < 20.5 -> Res.string.kurus
            bmi >= 27.0 -> Res.string.gemuk
            else -> Res.string.ideal
        }
    } else {
        when {
            bmi < 18.5 -> Res.string.kurus
            bmi >= 25.0 -> Res.string.gemuk
            else -> Res.string.ideal
        }
    }
}