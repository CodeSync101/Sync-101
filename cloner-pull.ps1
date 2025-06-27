
$repoUrl = 'https://github.com/CodeSync101/Sync-101.git'

$branches = git branch --format="%(refname:short)" | Where-Object { $_ -ne 'dridi01' }

foreach ($branch in $branches) {
    $folder = "backend-$branch"
    git clone --branch $branch --single-branch $repoUrl $folder
}
