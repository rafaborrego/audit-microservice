

$jiraTicket = $args[0]

if ( ! $jiraTicket ) { 
    Write-Host "Please provide the JIRA ticket number" 
} else {

  [xml]$pomXml = Get-Content ..\..\pom.xml
  1.0.0-SNAPSHOT = $pomXml.project.version
  1.0.0-SNAPSHOT = 1.0.0

  $timestamp = Get-Date  -Format yyyyMMddHHmm

  $fileName= 'V' + 1.0.0-SNAPSHOT + '_' + 0 + '_' + $timestamp + '__' + $jiraTicket + '.sql'

  New-Item $fileName -ItemType file
}
