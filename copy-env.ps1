# Usage: .\copy-env.ps1 [input_file] [output_file]
param(
    [string]$InputFile = ".env.dev.template",
    [string]$OutputFile = ".env"
)

# Read all lines
$lines = Get-Content $InputFile

$result = foreach ($line in $lines) {
    # Trim leading/trailing whitespace
    $trimmed = $line.Trim()
    # Skip empty lines and full-line comments
    if ($trimmed -eq "" -or $trimmed.StartsWith("#")) { continue }

    # If there's a quote before a #, ignore that # (it's in value)
    $firstQuote = $trimmed.IndexOf('"')
    $firstHash = $trimmed.IndexOf('#')

    if ($firstHash -ge 0) {
        if ($firstQuote -ge 0 -and $firstQuote -lt $firstHash) {
            # The hash is inside a quoted value; preserve the whole line
            $trimmed
        } else {
            # The hash is a comment, remove everything after
            $trimmed.Substring(0, $firstHash).TrimEnd()
        }
    } else {
        $trimmed
    }
}

# Write output, skipping blank lines
$result | Where-Object { $_ -ne "" } | Set-Content $OutputFile -Encoding UTF8

Write-Host "✅ Cleaned env file written to $OutputFile"
