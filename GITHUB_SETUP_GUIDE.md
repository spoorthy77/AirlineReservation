# GitHub Setup & Push Guide

## Overview
Your Airline Reservation project is now ready to be pushed to GitHub. All sensitive credentials have been removed and replaced with placeholders.

## Security Changes Made ✅

1. **Removed Hardcoded Credentials**
   - Removed database password from `DBConnection.java`
   - Removed database password from `BookFlight.java`
   - Added `.env` to `.gitignore` to prevent accidental commits

2. **Added Configuration Template**
   - Created `.env.example` file with all required configuration
   - Instructions for users to copy and customize

## Step-by-Step GitHub Push Instructions

### Step 1: Create Repository on GitHub

1. Go to [https://github.com/new](https://github.com/new)
2. Fill in the details:
   - **Repository name**: `AirlineReservation` (or your preferred name)
   - **Description**: "Airline Reservation System with AI Chatbot Integration"
   - **Visibility**: Public or Private (your choice)
3. **DO NOT** initialize with README (we already have one)
4. Click **"Create repository"**

### Step 2: Copy the Remote URL

After creating the repository, GitHub will show you commands. Copy the HTTPS URL:
```
https://github.com/YOUR_USERNAME/AirlineReservation.git
```

### Step 3: Configure Git Credentials (First Time Only)

In PowerShell, set your git user globally:

```powershell
git config --global user.name "Your Full Name"
git config --global user.email "your.email@github.com"
```

### Step 4: Add Remote Origin

Navigate to your project directory and add the remote:

```powershell
cd "c:\Users\m6793\OneDrive\Pictures\Documents\NetBeansProjects\AirlineReservation"
git remote add origin https://github.com/YOUR_USERNAME/AirlineReservation.git
```

### Step 5: Rename Branch to Main (Recommended)

```powershell
git branch -M main
```

### Step 6: Push to GitHub

```powershell
git push -u origin main
```

**First time?** You'll be prompted to authenticate. GitHub will ask for:
- **Username**: Your GitHub username
- **Password**: Your personal access token (NOT your GitHub password)

### Step 7: Verify on GitHub

1. Go to `https://github.com/YOUR_USERNAME/AirlineReservation`
2. You should see all your project files
3. Check that sensitive files are NOT visible:
   - No hardcoded passwords in the code
   - `.env` file should not be there (only `.env.example`)

## Creating GitHub Personal Access Token (If Needed)

If git asks for a password and you don't have a token:

1. Go to [GitHub Settings > Developer settings > Personal access tokens](https://github.com/settings/tokens)
2. Click **"Generate new token"** → **"Generate new token (classic)"**
3. Give it a name, e.g., "AirlineReservation-Push"
4. Select scopes:
   - ✅ `repo` (full control of private repositories)
5. Click **"Generate token"**
6. **Copy the token** (you'll see it only once)
7. Use this token as your password when git prompts

## Making Future Updates

After the initial push, making updates is simple:

```powershell
cd "c:\Users\m6793\OneDrive\Pictures\Documents\NetBeansProjects\AirlineReservation"

# Make your code changes...

# Stage changes
git add .

# Commit with a descriptive message
git commit -m "Your meaningful commit message"

# Push to GitHub
git push origin main
```

## Common Git Commands

```powershell
# Check status
git status

# See commit history
git log --oneline

# View changes
git diff

# Undo last commit (if not pushed)
git reset --soft HEAD~1

# Discard all changes
git reset --hard HEAD
```

## Setting Up Credentials for Future Sessions

To avoid entering credentials every time, you can:

### Option 1: Git Credential Manager (Recommended)
Windows comes with Git Credential Manager. Just authenticate once and it saves your credentials.

### Option 2: SSH Keys (More Advanced)
1. Generate SSH key:
   ```powershell
   ssh-keygen -t ed25519 -C "your.email@github.com"
   ```
2. Add public key to GitHub Settings > SSH Keys
3. Change remote URL to SSH:
   ```powershell
   git remote set-url origin git@github.com:YOUR_USERNAME/AirlineReservation.git
   ```

## Documentation Files to Update

Before your first push, consider updating these in your GitHub repository:

1. **README.md** - Already comprehensive ✅
2. **CHANGELOG.md** - Document version history
3. **CONTRIBUTING.md** - Guidelines for contributors

Example `.github/CONTRIBUTING.md`:
```markdown
# Contributing to Airline Reservation System

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/MyFeature`
3. Commit changes: `git commit -m 'Add MyFeature'`
4. Push branch: `git push origin feature/MyFeature`
5. Open a Pull Request

## Code Style
- Use Java naming conventions
- Add comments for complex logic
- Keep methods focused and small
```

## Troubleshooting

### Error: "repository not found"
- Check the repository name matches exactly
- Verify you have access to create repositories
- Check internet connection

### Error: "Authentication failed"
- Verify username is correct
- If using HTTPS, use a personal access token, not your password
- Check token has `repo` scope

### Error: "refused to merge unrelated histories"
- This shouldn't happen with a new repository
- If it does, use: `git pull origin main --allow-unrelated-histories`

### Files showing in git that shouldn't be
- Run: `git rm --cached filename`
- Update `.gitignore`
- Commit: `git commit -m "Remove untracked files"`

## Next Steps

1. **Add Project Description** on GitHub
   - Go to repository settings
   - Add a brief project description
   - Add relevant topics (java, chatbot, airline, swing, etc.)

2. **Enable GitHub Pages** (if you want a website)
   - Settings > Pages
   - Build from `/docs` or `/README`

3. **Add GitHub Actions** (for CI/CD)
   - Create `.github/workflows/maven.yml` for automated builds

4. **Create Issues Template**
   - Go to Settings > Issues > Set up templates
   - Create bug report and feature request templates

5. **Set Up Branch Protection** (if team project)
   - Settings > Branches > Add rule
   - Require reviews before merge

## Successful Push Indicator ✅

You'll know it's working when:
- ✅ `git push` command completes without errors
- ✅ GitHub shows "main" branch active
- ✅ All files appear in the web interface
- ✅ README.md displays properly
- ✅ No sensitive data visible in code

## Need Help?

- GitHub Docs: https://docs.github.com
- Git Documentation: https://git-scm.com/doc
- Common Issues: https://github.com/git/git/wiki/FAQ
