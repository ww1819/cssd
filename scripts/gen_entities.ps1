$ErrorActionPreference = 'Stop'
$sqlPath = 'E:\workspace\cssd\docs\sql\pgsql\V1__cssd_tables.sql'
$entDir = 'E:\workspace\cssd\backend\cssd-app\src\main\java\com\cssd\infra\entity'
$mapDir = 'E:\workspace\cssd\backend\cssd-app\src\main\java\com\cssd\infra\mapper'
New-Item -ItemType Directory -Force -Path $entDir, $mapDir | Out-Null

$sql = Get-Content -Raw -Encoding UTF8 $sqlPath
$typeMap = @{
  varchar = 'String'; integer = 'Integer'; smallint = 'Integer'
  date = 'java.time.LocalDate'; timestamptz = 'java.time.OffsetDateTime'
  jsonb = 'String'; numeric = 'java.math.BigDecimal'
}
$skip = @(
  'id','created_at','created_by','created_by_no','created_by_name',
  'updated_at','updated_by','updated_by_no','updated_by_name',
  'is_deleted','deleted_at','deleted_by','deleted_by_no','deleted_by_name',
  'create_channel','update_channel','delete_channel'
)

function Get-Camel([string]$s) {
  $p = $s.Split('_')
  $head = $p[0]
  $rest = ($p | Select-Object -Skip 1 | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join ''
  return $head + $rest
}
function Get-Pascal([string]$s) {
  return ($s.Split('_') | ForEach-Object { $_.Substring(0,1).ToUpper() + $_.Substring(1) }) -join ''
}

$utf8NoBom = New-Object System.Text.UTF8Encoding $false
$rx = [regex]::new('CREATE TABLE cssd\.(\w+)\s*\((.*?)CONSTRAINT', 'Singleline')
$matches = $rx.Matches($sql)
$count = 0
foreach ($m in $matches) {
  $table = $m.Groups[1].Value
  $body = $m.Groups[2].Value
  $cls = Get-Pascal $table
  $fields = New-Object System.Collections.Generic.List[string]
  $imports = New-Object System.Collections.Generic.HashSet[string]
  foreach ($line in $body -split "`n") {
    $line = $line.Trim().TrimEnd(',')
    if (-not $line -or $line.StartsWith('CONSTRAINT') -or $line.StartsWith('--')) { continue }
    if ($line -notmatch '^(\w+)\s+(\w+)') { continue }
    $col = $Matches[1]; $raw = $Matches[2]
    if ($skip -contains $col) { continue }
    $jtype = $typeMap[$raw]
    if (-not $jtype) { $jtype = 'String' }
    if ($jtype.StartsWith('java.')) {
      [void]$imports.Add($jtype)
      $jtype = $jtype.Split('.')[-1]
    }
    $fields.Add("    private $jtype $(Get-Camel $col);") | Out-Null
  }
  $impLines = New-Object System.Collections.Generic.List[string]
  $impLines.Add("import com.baomidou.mybatisplus.annotation.TableName;") | Out-Null
  foreach ($i in ($imports | Sort-Object)) { $impLines.Add("import $i;") | Out-Null }
  $impLines.Add("import lombok.Data;") | Out-Null
  $impLines.Add("import lombok.EqualsAndHashCode;") | Out-Null
  $impBlock = ($impLines -join "`r`n")
  $fieldLines = $fields -join "`r`n"
  $src = @"
package com.cssd.infra.entity;

$impBlock

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "$table", schema = "cssd")
public class $cls extends BaseEntity {
$fieldLines
}
"@
  [System.IO.File]::WriteAllText((Join-Path $entDir "$cls.java"), $src, $utf8NoBom)
  $mapper = @"
package com.cssd.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cssd.infra.entity.$cls;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${cls}Mapper extends BaseMapper<$cls> {
}
"@
  [System.IO.File]::WriteAllText((Join-Path $mapDir "${cls}Mapper.java"), $mapper, $utf8NoBom)
  Write-Output "$table -> $cls"
  $count++
}
Write-Output "generated $count"
